/*
 *
 *  * Copyright 2020 New Relic Corporation. All rights reserved.
 *  * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.newrelic.weave.utils;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.tree.TypeAnnotationNode;

/**
 * This class exists only to work around the fact that TypeAnnotationNode instances are NOT thread safe. If multiple
 * threads call {@link #accept(AnnotationVisitor)} at the same time there is a race condition that exists that has been
 * shown to cause VerifyErrors and other serious bytecode-related exceptions.
 *
 * By synchronizing on the {@link #accept(AnnotationVisitor)} method we are ensuring that any usage of this
 * TypeAnnotationNode in the agent can be accessed by multiple threads. One important thing to note is that this ties us
 * pretty strongly to an ASM version due to the fact that we had to override constructors in the original asm class. If
 * these constructors change or other accept() methods are added we will need to update accordingly.
 *
 * NOTE: If you are upgrading from org.ow2.asm:asm / org.ow2.asm:asm-tree make sure to double
 * check that this class hasn't changed between versions.
 */
public class SynchronizedTypeAnnotationNode extends TypeAnnotationNode {

    public SynchronizedTypeAnnotationNode(int typeRef, TypePath typePath, String desc) {
        super(WeaveUtils.ASM_API_LEVEL, typeRef, typePath, desc);
    }

    public SynchronizedTypeAnnotationNode(int api, int typeRef, TypePath typePath, String desc) {
        super(api, typeRef, typePath, desc);
    }

    /**
     * This method is synchronized in order to prevent issues with bytecode modification that
     * occur when multiple threads call accept on the same TypeAnnotationNode instance.
     */
    @Override
    public synchronized void accept(AnnotationVisitor av) {
        super.accept(av);
    }

}
