/*
 * Copyright (c) 2018, 2020, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * @test
 * @modules jdk.incubator.vector
 * @run testng/othervm -ea -esa -Xbatch Short256VectorTests
 */

// -- This file was mechanically generated: Do not edit! -- //

import jdk.incubator.vector.VectorShape;
import jdk.incubator.vector.VectorSpecies;
import jdk.incubator.vector.VectorShuffle;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.Vector;

import jdk.incubator.vector.ShortVector;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.Integer;
import java.util.List;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Test
public class Short256VectorTests extends AbstractVectorTest {

    static final VectorSpecies<Short> SPECIES =
                ShortVector.SPECIES_256;

    static final int INVOC_COUNT = Integer.getInteger("jdk.incubator.vector.test.loop-iterations", 100);


    static final int BUFFER_REPS = Integer.getInteger("jdk.incubator.vector.test.buffer-vectors", 25000 / 256);

    static final int BUFFER_SIZE = Integer.getInteger("jdk.incubator.vector.test.buffer-size", BUFFER_REPS * (256 / 8));

    interface FUnOp {
        short apply(short a);
    }

    static void assertArraysEquals(short[] a, short[] r, FUnOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i]));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a[i]), "at index #" + i + ", input = " + a[i]);
        }
    }

    interface FUnArrayOp {
        short[] apply(short a);
    }

    static void assertArraysEquals(short[] a, short[] r, FUnArrayOp f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a[i]));
            }
        } catch (AssertionError e) {
            short[] ref = f.apply(a[i]);
            short[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(ref, res, "(ref: " + Arrays.toString(ref)
              + ", res: " + Arrays.toString(res)
              + "), at index #" + i);
        }
    }

    static void assertArraysEquals(short[] a, short[] r, boolean[] mask, FUnOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], mask[i % SPECIES.length()] ? f.apply(a[i]) : a[i]);
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], mask[i % SPECIES.length()] ? f.apply(a[i]) : a[i], "at index #" + i + ", input = " + a[i] + ", mask = " + mask[i % SPECIES.length()]);
        }
    }

    interface FReductionOp {
        short apply(short[] a, int idx);
    }

    interface FReductionAllOp {
        short apply(short[] a);
    }

    static void assertReductionArraysEquals(short[] a, short[] b, short c,
                                            FReductionOp f, FReductionAllOp fa) {
        int i = 0;
        try {
            Assert.assertEquals(c, fa.apply(a));
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(b[i], f.apply(a, i));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(c, fa.apply(a), "Final result is incorrect!");
            Assert.assertEquals(b[i], f.apply(a, i), "at index #" + i);
        }
    }

    interface FReductionMaskedOp {
        short apply(short[] a, int idx, boolean[] mask);
    }

    interface FReductionAllMaskedOp {
        short apply(short[] a, boolean[] mask);
    }

    static void assertReductionArraysEqualsMasked(short[] a, short[] b, short c, boolean[] mask,
                                            FReductionMaskedOp f, FReductionAllMaskedOp fa) {
        int i = 0;
        try {
            Assert.assertEquals(c, fa.apply(a, mask));
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(b[i], f.apply(a, i, mask));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(c, fa.apply(a, mask), "Final result is incorrect!");
            Assert.assertEquals(b[i], f.apply(a, i, mask), "at index #" + i);
        }
    }

    interface FBoolReductionOp {
        boolean apply(boolean[] a, int idx);
    }

    static void assertReductionBoolArraysEquals(boolean[] a, boolean[] b, FBoolReductionOp f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(b[i], f.apply(a, i));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(b[i], f.apply(a, i), "at index #" + i);
        }
    }

    static void assertInsertArraysEquals(short[] a, short[] b, short element, int index) {
        int i = 0;
        try {
            for (; i < a.length; i += 1) {
                if(i%SPECIES.length() == index) {
                    Assert.assertEquals(b[i], element);
                } else {
                    Assert.assertEquals(b[i], a[i]);
                }
            }
        } catch (AssertionError e) {
            if (i%SPECIES.length() == index) {
                Assert.assertEquals(b[i], element, "at index #" + i);
            } else {
                Assert.assertEquals(b[i], a[i], "at index #" + i);
            }
        }
    }

    static void assertRearrangeArraysEquals(short[] a, short[] r, int[] order, int vector_len) {
        int i = 0, j = 0;
        try {
            for (; i < a.length; i += vector_len) {
                for (j = 0; j < vector_len; j++) {
                    Assert.assertEquals(r[i+j], a[i+order[i+j]]);
                }
            }
        } catch (AssertionError e) {
            int idx = i + j;
            Assert.assertEquals(r[i+j], a[i+order[i+j]], "at index #" + idx + ", input = " + a[i+order[i+j]]);
        }
    }

    static void assertBroadcastArraysEquals(short[]a, short[]r) {
        int i = 0;
        for (; i < a.length; i += SPECIES.length()) {
            int idx = i;
            for (int j = idx; j < (idx + SPECIES.length()); j++)
                a[j]=a[idx];
        }

        try {
            for (i = 0; i < a.length; i++) {
                Assert.assertEquals(r[i], a[i]);
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], a[i], "at index #" + i + ", input = " + a[i]);
        }
    }

    interface FBinOp {
        short apply(short a, short b);
    }

    interface FBinMaskOp {
        short apply(short a, short b, boolean m);

        static FBinMaskOp lift(FBinOp f) {
            return (a, b, m) -> m ? f.apply(a, b) : a;
        }
    }

    static void assertArraysEquals(short[] a, short[] b, short[] r, FBinOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[i]));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a[i], b[i]), "(" + a[i] + ", " + b[i] + ") at index #" + i);
        }
    }

    static void assertBroadcastArraysEquals(short[] a, short[] b, short[] r, FBinOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()]));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()]),
                                "(" + a[i] + ", " + b[(i / SPECIES.length()) * SPECIES.length()] + ") at index #" + i);
        }
    }

    static void assertBroadcastLongArraysEquals(short[] a, short[] b, short[] r, FBinOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], (short)((long)b[(i / SPECIES.length()) * SPECIES.length()])));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a[i], (short)((long)b[(i / SPECIES.length()) * SPECIES.length()])),
                                "(" + a[i] + ", " + b[(i / SPECIES.length()) * SPECIES.length()] + ") at index #" + i);
        }
    }

    static void assertArraysEquals(short[] a, short[] b, short[] r, boolean[] mask, FBinOp f) {
        assertArraysEquals(a, b, r, mask, FBinMaskOp.lift(f));
    }

    static void assertArraysEquals(short[] a, short[] b, short[] r, boolean[] mask, FBinMaskOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[i], mask[i % SPECIES.length()]));
            }
        } catch (AssertionError err) {
            Assert.assertEquals(r[i], f.apply(a[i], b[i], mask[i % SPECIES.length()]), "at index #" + i + ", input1 = " + a[i] + ", input2 = " + b[i] + ", mask = " + mask[i % SPECIES.length()]);
        }
    }

    static void assertBroadcastArraysEquals(short[] a, short[] b, short[] r, boolean[] mask, FBinOp f) {
        assertBroadcastArraysEquals(a, b, r, mask, FBinMaskOp.lift(f));
    }

    static void assertBroadcastArraysEquals(short[] a, short[] b, short[] r, boolean[] mask, FBinMaskOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()], mask[i % SPECIES.length()]));
            }
        } catch (AssertionError err) {
            Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()],
                                mask[i % SPECIES.length()]), "at index #" + i + ", input1 = " + a[i] +
                                ", input2 = " + b[(i / SPECIES.length()) * SPECIES.length()] + ", mask = " +
                                mask[i % SPECIES.length()]);
        }
    }

    static void assertBroadcastLongArraysEquals(short[] a, short[] b, short[] r, boolean[] mask, FBinOp f) {
        assertBroadcastLongArraysEquals(a, b, r, mask, FBinMaskOp.lift(f));
    }

    static void assertBroadcastLongArraysEquals(short[] a, short[] b, short[] r, boolean[] mask, FBinMaskOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], (short)((long)b[(i / SPECIES.length()) * SPECIES.length()]), mask[i % SPECIES.length()]));
            }
        } catch (AssertionError err) {
            Assert.assertEquals(r[i], f.apply(a[i], (short)((long)b[(i / SPECIES.length()) * SPECIES.length()]),
                                mask[i % SPECIES.length()]), "at index #" + i + ", input1 = " + a[i] +
                                ", input2 = " + b[(i / SPECIES.length()) * SPECIES.length()] + ", mask = " +
                                mask[i % SPECIES.length()]);
        }
    }

    static void assertShiftArraysEquals(short[] a, short[] b, short[] r, FBinOp f) {
        int i = 0;
        int j = 0;
        try {
            for (; j < a.length; j += SPECIES.length()) {
                for (i = 0; i < SPECIES.length(); i++) {
                    Assert.assertEquals(r[i+j], f.apply(a[i+j], b[j]));
                }
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i+j], f.apply(a[i+j], b[j]), "at index #" + i + ", " + j);
        }
    }

    static void assertShiftArraysEquals(short[] a, short[] b, short[] r, boolean[] mask, FBinOp f) {
        assertShiftArraysEquals(a, b, r, mask, FBinMaskOp.lift(f));
    }

    static void assertShiftArraysEquals(short[] a, short[] b, short[] r, boolean[] mask, FBinMaskOp f) {
        int i = 0;
        int j = 0;
        try {
            for (; j < a.length; j += SPECIES.length()) {
                for (i = 0; i < SPECIES.length(); i++) {
                    Assert.assertEquals(r[i+j], f.apply(a[i+j], b[j], mask[i]));
                }
            }
        } catch (AssertionError err) {
            Assert.assertEquals(r[i+j], f.apply(a[i+j], b[j], mask[i]), "at index #" + i + ", input1 = " + a[i+j] + ", input2 = " + b[j] + ", mask = " + mask[i]);
        }
    }

    interface FTernOp {
        short apply(short a, short b, short c);
    }

    interface FTernMaskOp {
        short apply(short a, short b, short c, boolean m);

        static FTernMaskOp lift(FTernOp f) {
            return (a, b, c, m) -> m ? f.apply(a, b, c) : a;
        }
    }

    static void assertArraysEquals(short[] a, short[] b, short[] c, short[] r, FTernOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[i], c[i]));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a[i], b[i], c[i]), "at index #" + i + ", input1 = " + a[i] + ", input2 = " + b[i] + ", input3 = " + c[i]);
        }
    }

    static void assertArraysEquals(short[] a, short[] b, short[] c, short[] r, boolean[] mask, FTernOp f) {
        assertArraysEquals(a, b, c, r, mask, FTernMaskOp.lift(f));
    }

    static void assertArraysEquals(short[] a, short[] b, short[] c, short[] r, boolean[] mask, FTernMaskOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[i], c[i], mask[i % SPECIES.length()]));
            }
        } catch (AssertionError err) {
            Assert.assertEquals(r[i], f.apply(a[i], b[i], c[i], mask[i % SPECIES.length()]), "at index #" + i + ", input1 = " + a[i] + ", input2 = "
              + b[i] + ", input3 = " + c[i] + ", mask = " + mask[i % SPECIES.length()]);
        }
    }

    static void assertBroadcastArraysEquals(short[] a, short[] b, short[] c, short[] r, FTernOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[i], c[(i / SPECIES.length()) * SPECIES.length()]));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a[i], b[i], c[(i / SPECIES.length()) * SPECIES.length()]), "at index #" +
                                i + ", input1 = " + a[i] + ", input2 = " + b[i] + ", input3 = " +
                                c[(i / SPECIES.length()) * SPECIES.length()]);
        }
    }

    static void assertAltBroadcastArraysEquals(short[] a, short[] b, short[] c, short[] r, FTernOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()], c[i]));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()], c[i]), "at index #" +
                                i + ", input1 = " + a[i] + ", input2 = " +
                                b[(i / SPECIES.length()) * SPECIES.length()] + ",  input3 = " + c[i]);
        }
    }

    static void assertBroadcastArraysEquals(short[] a, short[] b, short[] c, short[] r, boolean[] mask,
                                            FTernOp f) {
        assertBroadcastArraysEquals(a, b, c, r, mask, FTernMaskOp.lift(f));
    }

    static void assertBroadcastArraysEquals(short[] a, short[] b, short[] c, short[] r, boolean[] mask,
                                            FTernMaskOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[i], c[(i / SPECIES.length()) * SPECIES.length()],
                                    mask[i % SPECIES.length()]));
            }
        } catch (AssertionError err) {
            Assert.assertEquals(r[i], f.apply(a[i], b[i], c[(i / SPECIES.length()) * SPECIES.length()],
                                mask[i % SPECIES.length()]), "at index #" + i + ", input1 = " + a[i] + ", input2 = " +
                                b[i] + ", input3 = " + c[(i / SPECIES.length()) * SPECIES.length()] + ", mask = " +
                                mask[i % SPECIES.length()]);
        }
    }

    static void assertAltBroadcastArraysEquals(short[] a, short[] b, short[] c, short[] r, boolean[] mask,
                                            FTernOp f) {
        assertAltBroadcastArraysEquals(a, b, c, r, mask, FTernMaskOp.lift(f));
    }

    static void assertAltBroadcastArraysEquals(short[] a, short[] b, short[] c, short[] r, boolean[] mask,
                                            FTernMaskOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()], c[i],
                                    mask[i % SPECIES.length()]));
            }
        } catch (AssertionError err) {
            Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()], c[i],
                                mask[i % SPECIES.length()]), "at index #" + i + ", input1 = " + a[i] +
                                ", input2 = " + b[(i / SPECIES.length()) * SPECIES.length()] +
                                ", input3 = " + c[i] + ", mask = " + mask[i % SPECIES.length()]);
        }
    }

    static void assertDoubleBroadcastArraysEquals(short[] a, short[] b, short[] c, short[] r, FTernOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()],
                                    c[(i / SPECIES.length()) * SPECIES.length()]));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()],
                                c[(i / SPECIES.length()) * SPECIES.length()]), "at index #" + i + ", input1 = " + a[i]
                                + ", input2 = " + b[(i / SPECIES.length()) * SPECIES.length()] + ", input3 = " +
                                c[(i / SPECIES.length()) * SPECIES.length()]);
        }
    }

    static void assertDoubleBroadcastArraysEquals(short[] a, short[] b, short[] c, short[] r, boolean[] mask,
                                                  FTernOp f) {
        assertDoubleBroadcastArraysEquals(a, b, c, r, mask, FTernMaskOp.lift(f));
    }

    static void assertDoubleBroadcastArraysEquals(short[] a, short[] b, short[] c, short[] r, boolean[] mask,
                                                  FTernMaskOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()],
                                    c[(i / SPECIES.length()) * SPECIES.length()], mask[i % SPECIES.length()]));
            }
        } catch (AssertionError err) {
            Assert.assertEquals(r[i], f.apply(a[i], b[(i / SPECIES.length()) * SPECIES.length()],
                                c[(i / SPECIES.length()) * SPECIES.length()], mask[i % SPECIES.length()]), "at index #"
                                + i + ", input1 = " + a[i] + ", input2 = " + b[(i / SPECIES.length()) * SPECIES.length()] +
                                ", input3 = " + c[(i / SPECIES.length()) * SPECIES.length()] + ", mask = " +
                                mask[i % SPECIES.length()]);
        }
    }



    interface FBinArrayOp {
        short apply(short[] a, int b);
    }

    static void assertArraysEquals(short[] a, short[] r, FBinArrayOp f) {
        int i = 0;
        try {
            for (; i < a.length; i++) {
                Assert.assertEquals(r[i], f.apply(a, i));
            }
        } catch (AssertionError e) {
            Assert.assertEquals(r[i], f.apply(a,i), "at index #" + i);
        }
    }

    interface FGatherScatterOp {
        short[] apply(short[] a, int ix, int[] b, int iy);
    }

    static void assertArraysEquals(short[] a, int[] b, short[] r, FGatherScatterOp f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a, i, b, i));
            }
        } catch (AssertionError e) {
            short[] ref = f.apply(a, i, b, i);
            short[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(res, ref,
              "(ref: " + Arrays.toString(ref) + ", res: " + Arrays.toString(res) + ", a: "
              + Arrays.toString(Arrays.copyOfRange(a, i, i+SPECIES.length()))
              + ", b: "
              + Arrays.toString(Arrays.copyOfRange(b, i, i+SPECIES.length()))
              + " at index #" + i);
        }
    }

    interface FGatherMaskedOp {
        short[] apply(short[] a, int ix, boolean[] mask, int[] b, int iy);
    }

    interface FScatterMaskedOp {
        short[] apply(short[] r, short[] a, int ix, boolean[] mask, int[] b, int iy);
    }

    static void assertArraysEquals(short[] a, int[] b, short[] r, boolean[] mask, FGatherMaskedOp f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a, i, mask, b, i));
            }
        } catch (AssertionError e) {
            short[] ref = f.apply(a, i, mask, b, i);
            short[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(ref, res,
              "(ref: " + Arrays.toString(ref) + ", res: " + Arrays.toString(res) + ", a: "
              + Arrays.toString(Arrays.copyOfRange(a, i, i+SPECIES.length()))
              + ", b: "
              + Arrays.toString(Arrays.copyOfRange(b, i, i+SPECIES.length()))
              + ", mask: "
              + Arrays.toString(mask)
              + " at index #" + i);
        }
    }

    static void assertArraysEquals(short[] a, int[] b, short[] r, boolean[] mask, FScatterMaskedOp f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(r, a, i, mask, b, i));
            }
        } catch (AssertionError e) {
            short[] ref = f.apply(r, a, i, mask, b, i);
            short[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(ref, res,
              "(ref: " + Arrays.toString(ref) + ", res: " + Arrays.toString(res) + ", a: "
              + Arrays.toString(Arrays.copyOfRange(a, i, i+SPECIES.length()))
              + ", b: "
              + Arrays.toString(Arrays.copyOfRange(b, i, i+SPECIES.length()))
              + ", r: "
              + Arrays.toString(Arrays.copyOfRange(r, i, i+SPECIES.length()))
              + ", mask: "
              + Arrays.toString(mask)
              + " at index #" + i);
        }
    }

    interface FLaneOp {
        short[] apply(short[] a, int origin, int idx);
    }

    static void assertArraysEquals(short[] a, short[] r, int origin, FLaneOp f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a, origin, i));
            }
        } catch (AssertionError e) {
            short[] ref = f.apply(a, origin, i);
            short[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(ref, res, "(ref: " + Arrays.toString(ref)
              + ", res: " + Arrays.toString(res)
              + "), at index #" + i);
        }
    }

    interface FLaneBop {
        short[] apply(short[] a, short[] b, int origin, int idx);
    }

    static void assertArraysEquals(short[] a, short[] b, short[] r, int origin, FLaneBop f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a, b, origin, i));
            }
        } catch (AssertionError e) {
            short[] ref = f.apply(a, b, origin, i);
            short[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(ref, res, "(ref: " + Arrays.toString(ref)
              + ", res: " + Arrays.toString(res)
              + "), at index #" + i
              + ", at origin #" + origin);
        }
    }

    interface FLaneMaskedBop {
        short[] apply(short[] a, short[] b, int origin, boolean[] mask, int idx);
    }

    static void assertArraysEquals(short[] a, short[] b, short[] r, int origin, boolean[] mask, FLaneMaskedBop f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a, b, origin, mask, i));
            }
        } catch (AssertionError e) {
            short[] ref = f.apply(a, b, origin, mask, i);
            short[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(ref, res, "(ref: " + Arrays.toString(ref)
              + ", res: " + Arrays.toString(res)
              + "), at index #" + i
              + ", at origin #" + origin);
        }
    }

    interface FLanePartBop {
        short[] apply(short[] a, short[] b, int origin, int part, int idx);
    }

    static void assertArraysEquals(short[] a, short[] b, short[] r, int origin, int part, FLanePartBop f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a, b, origin, part, i));
            }
        } catch (AssertionError e) {
            short[] ref = f.apply(a, b, origin, part, i);
            short[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(ref, res, "(ref: " + Arrays.toString(ref)
              + ", res: " + Arrays.toString(res)
              + "), at index #" + i
              + ", at origin #" + origin
              + ", with part #" + part);
        }
    }

    interface FLanePartMaskedBop {
        short[] apply(short[] a, short[] b, int origin, int part, boolean[] mask, int idx);
    }

    static void assertArraysEquals(short[] a, short[] b, short[] r, int origin, int part, boolean[] mask, FLanePartMaskedBop f) {
        int i = 0;
        try {
            for (; i < a.length; i += SPECIES.length()) {
                Assert.assertEquals(Arrays.copyOfRange(r, i, i+SPECIES.length()),
                  f.apply(a, b, origin, part, mask, i));
            }
        } catch (AssertionError e) {
            short[] ref = f.apply(a, b, origin, part, mask, i);
            short[] res = Arrays.copyOfRange(r, i, i+SPECIES.length());
            Assert.assertEquals(ref, res, "(ref: " + Arrays.toString(ref)
              + ", res: " + Arrays.toString(res)
              + "), at index #" + i
              + ", at origin #" + origin
              + ", with part #" + part);
        }
    }

    static short bits(short e) {
        return  e;
    }

    static final List<IntFunction<short[]>> SHORT_GENERATORS = List.of(
            withToString("short[-i * 5]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (short)(-i * 5));
            }),
            withToString("short[i * 5]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (short)(i * 5));
            }),
            withToString("short[i + 1]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (((short)(i + 1) == 0) ? 1 : (short)(i + 1)));
            }),
            withToString("short[cornerCaseValue(i)]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> cornerCaseValue(i));
            })
    );

    // Create combinations of pairs
    // @@@ Might be sensitive to order e.g. div by 0
    static final List<List<IntFunction<short[]>>> SHORT_GENERATOR_PAIRS =
        Stream.of(SHORT_GENERATORS.get(0)).
                flatMap(fa -> SHORT_GENERATORS.stream().skip(1).map(fb -> List.of(fa, fb))).
                collect(Collectors.toList());

    @DataProvider
    public Object[][] boolUnaryOpProvider() {
        return BOOL_ARRAY_GENERATORS.stream().
                map(f -> new Object[]{f}).
                toArray(Object[][]::new);
    }

    static final List<List<IntFunction<short[]>>> SHORT_GENERATOR_TRIPLES =
        SHORT_GENERATOR_PAIRS.stream().
                flatMap(pair -> SHORT_GENERATORS.stream().map(f -> List.of(pair.get(0), pair.get(1), f))).
                collect(Collectors.toList());

    @DataProvider
    public Object[][] shortBinaryOpProvider() {
        return SHORT_GENERATOR_PAIRS.stream().map(List::toArray).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] shortIndexedOpProvider() {
        return SHORT_GENERATOR_PAIRS.stream().map(List::toArray).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] shortBinaryOpMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> SHORT_GENERATOR_PAIRS.stream().map(lfa -> {
                    return Stream.concat(lfa.stream(), Stream.of(fm)).toArray();
                })).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] shortTernaryOpProvider() {
        return SHORT_GENERATOR_TRIPLES.stream().map(List::toArray).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] shortTernaryOpMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> SHORT_GENERATOR_TRIPLES.stream().map(lfa -> {
                    return Stream.concat(lfa.stream(), Stream.of(fm)).toArray();
                })).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] shortUnaryOpProvider() {
        return SHORT_GENERATORS.stream().
                map(f -> new Object[]{f}).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] shortUnaryOpMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> SHORT_GENERATORS.stream().map(fa -> {
                    return new Object[] {fa, fm};
                })).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] shortUnaryOpShuffleProvider() {
        return INT_SHUFFLE_GENERATORS.stream().
                flatMap(fs -> SHORT_GENERATORS.stream().map(fa -> {
                    return new Object[] {fa, fs};
                })).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] shortUnaryOpIndexProvider() {
        return INT_INDEX_GENERATORS.stream().
                flatMap(fs -> SHORT_GENERATORS.stream().map(fa -> {
                    return new Object[] {fa, fs};
                })).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] shortUnaryMaskedOpIndexProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
          flatMap(fs -> INT_INDEX_GENERATORS.stream().flatMap(fm ->
            SHORT_GENERATORS.stream().map(fa -> {
                    return new Object[] {fa, fm, fs};
            }))).
            toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] scatterMaskedOpIndexProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
          flatMap(fs -> INT_INDEX_GENERATORS.stream().flatMap(fm ->
            SHORT_GENERATORS.stream().flatMap(fn ->
              SHORT_GENERATORS.stream().map(fa -> {
                    return new Object[] {fa, fn, fm, fs};
            })))).
            toArray(Object[][]::new);
    }

    static final List<IntFunction<short[]>> SHORT_COMPARE_GENERATORS = List.of(
            withToString("short[i]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (short)i);
            }),
            withToString("short[i + 1]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (short)(i + 1));
            }),
            withToString("short[i - 2]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> (short)(i - 2));
            }),
            withToString("short[zigZag(i)]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> i%3 == 0 ? (short)i : (i%3 == 1 ? (short)(i + 1) : (short)(i - 2)));
            }),
            withToString("short[cornerCaseValue(i)]", (int s) -> {
                return fill(s * BUFFER_REPS,
                            i -> cornerCaseValue(i));
            })
    );

    static final List<List<IntFunction<short[]>>> SHORT_TEST_GENERATOR_ARGS =
        SHORT_COMPARE_GENERATORS.stream().
                map(fa -> List.of(fa)).
                collect(Collectors.toList());

    @DataProvider
    public Object[][] shortTestOpProvider() {
        return SHORT_TEST_GENERATOR_ARGS.stream().map(List::toArray).
                toArray(Object[][]::new);
    }

    static final List<List<IntFunction<short[]>>> SHORT_COMPARE_GENERATOR_PAIRS =
        SHORT_COMPARE_GENERATORS.stream().
                flatMap(fa -> SHORT_COMPARE_GENERATORS.stream().map(fb -> List.of(fa, fb))).
                collect(Collectors.toList());

    @DataProvider
    public Object[][] shortCompareOpProvider() {
        return SHORT_COMPARE_GENERATOR_PAIRS.stream().map(List::toArray).
                toArray(Object[][]::new);
    }

    @DataProvider
    public Object[][] shortCompareOpMaskProvider() {
        return BOOLEAN_MASK_GENERATORS.stream().
                flatMap(fm -> SHORT_COMPARE_GENERATOR_PAIRS.stream().map(lfa -> {
                    return Stream.concat(lfa.stream(), Stream.of(fm)).toArray();
                })).
                toArray(Object[][]::new);
    }

    interface ToShortF {
        short apply(int i);
    }

    static short[] fill(int s , ToShortF f) {
        return fill(new short[s], f);
    }

    static short[] fill(short[] a, ToShortF f) {
        for (int i = 0; i < a.length; i++) {
            a[i] = f.apply(i);
        }
        return a;
    }

    static short cornerCaseValue(int i) {
        switch(i % 5) {
            case 0:
                return Short.MAX_VALUE;
            case 1:
                return Short.MIN_VALUE;
            case 2:
                return Short.MIN_VALUE;
            case 3:
                return Short.MAX_VALUE;
            default:
                return (short)0;
        }
    }
   static short get(short[] a, int i) {
       return (short) a[i];
   }

   static final IntFunction<short[]> fr = (vl) -> {
        int length = BUFFER_REPS * vl;
        return new short[length];
    };

    static final IntFunction<boolean[]> fmr = (vl) -> {
        int length = BUFFER_REPS * vl;
        return new boolean[length];
    };

    static void replaceZero(short[] a, short v) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] == 0) {
                a[i] = v;
            }
        }
    }

    static void replaceZero(short[] a, boolean[] mask, short v) {
        for (int i = 0; i < a.length; i++) {
            if (mask[i % mask.length] && a[i] == 0) {
                a[i] = v;
            }
        }
    }

    @Test
    static void smokeTest1() {
        ShortVector three = ShortVector.broadcast(SPECIES, (byte)-3);
        ShortVector three2 = (ShortVector) SPECIES.broadcast(-3);
        assert(three.eq(three2).allTrue());
        ShortVector three3 = three2.broadcast(1).broadcast(-3);
        assert(three.eq(three3).allTrue());
        int scale = 2;
        Class<?> ETYPE = short.class;
        if (ETYPE == double.class || ETYPE == long.class)
            scale = 1000000;
        else if (ETYPE == byte.class && SPECIES.length() >= 64)
            scale = 1;
        ShortVector higher = three.addIndex(scale);
        VectorMask<Short> m = three.compare(VectorOperators.LE, higher);
        assert(m.allTrue());
        m = higher.min((short)-1).test(VectorOperators.IS_NEGATIVE);
        assert(m.allTrue());
        short max = higher.reduceLanes(VectorOperators.MAX);
        assert(max == -3 + scale * (SPECIES.length()-1));
    }

    private static short[]
    bothToArray(ShortVector a, ShortVector b) {
        short[] r = new short[a.length() + b.length()];
        a.intoArray(r, 0);
        b.intoArray(r, a.length());
        return r;
    }

    @Test
    static void smokeTest2() {
        // Do some zipping and shuffling.
        ShortVector io = (ShortVector) SPECIES.broadcast(0).addIndex(1);
        ShortVector io2 = (ShortVector) VectorShuffle.iota(SPECIES,0,1,false).toVector();
        Assert.assertEquals(io, io2);
        ShortVector a = io.add((short)1); //[1,2]
        ShortVector b = a.neg();  //[-1,-2]
        short[] abValues = bothToArray(a,b); //[1,2,-1,-2]
        VectorShuffle<Short> zip0 = VectorShuffle.makeZip(SPECIES, 0);
        VectorShuffle<Short> zip1 = VectorShuffle.makeZip(SPECIES, 1);
        ShortVector zab0 = a.rearrange(zip0,b); //[1,-1]
        ShortVector zab1 = a.rearrange(zip1,b); //[2,-2]
        short[] zabValues = bothToArray(zab0, zab1); //[1,-1,2,-2]
        // manually zip
        short[] manual = new short[zabValues.length];
        for (int i = 0; i < manual.length; i += 2) {
            manual[i+0] = abValues[i/2];
            manual[i+1] = abValues[a.length() + i/2];
        }
        Assert.assertEquals(Arrays.toString(zabValues), Arrays.toString(manual));
        VectorShuffle<Short> unz0 = VectorShuffle.makeUnzip(SPECIES, 0);
        VectorShuffle<Short> unz1 = VectorShuffle.makeUnzip(SPECIES, 1);
        ShortVector uab0 = zab0.rearrange(unz0,zab1);
        ShortVector uab1 = zab0.rearrange(unz1,zab1);
        short[] abValues1 = bothToArray(uab0, uab1);
        Assert.assertEquals(Arrays.toString(abValues), Arrays.toString(abValues1));
    }

    static void iotaShuffle() {
        ShortVector io = (ShortVector) SPECIES.broadcast(0).addIndex(1);
        ShortVector io2 = (ShortVector) VectorShuffle.iota(SPECIES, 0 , 1, false).toVector();
        Assert.assertEquals(io, io2);
    }

    @Test
    // Test all shuffle related operations.
    static void shuffleTest() {
        // To test backend instructions, make sure that C2 is used.
        for (int loop = 0; loop < INVOC_COUNT * INVOC_COUNT; loop++) {
            iotaShuffle();
        }
    }

    @Test
    void viewAsIntegeralLanesTest() {
        Vector<?> asIntegral = SPECIES.zero().viewAsIntegralLanes();
        Assert.assertEquals(asIntegral.species(), SPECIES);
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    void viewAsFloatingLanesTest() {
        SPECIES.zero().viewAsFloatingLanes();
    }

    @Test
    // Test div by 0.
    static void bitwiseDivByZeroSmokeTest() {
        try {
            ShortVector a = (ShortVector) SPECIES.broadcast(0).addIndex(1);
            ShortVector b = (ShortVector) SPECIES.broadcast(0);
            a.div(b);
            Assert.fail();
        } catch (ArithmeticException e) {
        }

        try {
            ShortVector a = (ShortVector) SPECIES.broadcast(0).addIndex(1);
            ShortVector b = (ShortVector) SPECIES.broadcast(0);
            VectorMask<Short> m = a.lt((short) 1);
            a.div(b, m);
            Assert.fail();
        } catch (ArithmeticException e) {
        }
    }
    static short ADD(short a, short b) {
        return (short)(a + b);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void ADDShort256VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.ADD, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Short256VectorTests::ADD);
    }
    static short add(short a, short b) {
        return (short)(a + b);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void addShort256VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
            av.add(bv).intoArray(r, i);
        }

        assertArraysEquals(a, b, r, Short256VectorTests::add);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void ADDShort256VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.ADD, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, mask, Short256VectorTests::ADD);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void addShort256VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
            av.add(bv, vmask).intoArray(r, i);
        }

        assertArraysEquals(a, b, r, mask, Short256VectorTests::add);
    }
    static short SUB(short a, short b) {
        return (short)(a - b);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void SUBShort256VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.SUB, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Short256VectorTests::SUB);
    }
    static short sub(short a, short b) {
        return (short)(a - b);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void subShort256VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
            av.sub(bv).intoArray(r, i);
        }

        assertArraysEquals(a, b, r, Short256VectorTests::sub);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void SUBShort256VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.SUB, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, mask, Short256VectorTests::SUB);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void subShort256VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
            av.sub(bv, vmask).intoArray(r, i);
        }

        assertArraysEquals(a, b, r, mask, Short256VectorTests::sub);
    }
    static short MUL(short a, short b) {
        return (short)(a * b);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void MULShort256VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.MUL, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Short256VectorTests::MUL);
    }
    static short mul(short a, short b) {
        return (short)(a * b);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void mulShort256VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
            av.mul(bv).intoArray(r, i);
        }

        assertArraysEquals(a, b, r, Short256VectorTests::mul);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void MULShort256VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.MUL, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, mask, Short256VectorTests::MUL);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void mulShort256VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
            av.mul(bv, vmask).intoArray(r, i);
        }

        assertArraysEquals(a, b, r, mask, Short256VectorTests::mul);
    }



    static short DIV(short a, short b) {
        return (short)(a / b);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void DIVShort256VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        replaceZero(b, (short) 1);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.DIV, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Short256VectorTests::DIV);
    }
    static short div(short a, short b) {
        return (short)(a / b);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void divShort256VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        replaceZero(b, (short) 1);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.div(bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Short256VectorTests::div);
    }



    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void DIVShort256VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        replaceZero(b, mask, (short) 1);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.DIV, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, mask, Short256VectorTests::DIV);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void divShort256VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        replaceZero(b, mask, (short) 1);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.div(bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, mask, Short256VectorTests::div);
    }

    static short FIRST_NONZERO(short a, short b) {
        return (short)((a)!=0?a:b);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void FIRST_NONZEROShort256VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.FIRST_NONZERO, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Short256VectorTests::FIRST_NONZERO);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void FIRST_NONZEROShort256VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.FIRST_NONZERO, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, mask, Short256VectorTests::FIRST_NONZERO);
    }

    static short AND(short a, short b) {
        return (short)(a & b);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void ANDShort256VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.AND, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Short256VectorTests::AND);
    }
    static short and(short a, short b) {
        return (short)(a & b);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void andShort256VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
            av.and(bv).intoArray(r, i);
        }

        assertArraysEquals(a, b, r, Short256VectorTests::and);
    }



    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void ANDShort256VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.AND, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, mask, Short256VectorTests::AND);
    }


    static short AND_NOT(short a, short b) {
        return (short)(a & ~b);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void AND_NOTShort256VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.AND_NOT, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Short256VectorTests::AND_NOT);
    }



    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void AND_NOTShort256VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.AND_NOT, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, mask, Short256VectorTests::AND_NOT);
    }


    static short OR(short a, short b) {
        return (short)(a | b);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void ORShort256VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.OR, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Short256VectorTests::OR);
    }
    static short or(short a, short b) {
        return (short)(a | b);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void orShort256VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
            av.or(bv).intoArray(r, i);
        }

        assertArraysEquals(a, b, r, Short256VectorTests::or);
    }



    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void ORShort256VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.OR, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, mask, Short256VectorTests::OR);
    }


    static short XOR(short a, short b) {
        return (short)(a ^ b);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void XORShort256VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.XOR, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Short256VectorTests::XOR);
    }



    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void XORShort256VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.XOR, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, mask, Short256VectorTests::XOR);
    }


    @Test(dataProvider = "shortBinaryOpProvider")
    static void addShort256VectorTestsBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.add(b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, Short256VectorTests::add);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void addShort256VectorTestsBroadcastMaskedSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.add(b[i], vmask).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, mask, Short256VectorTests::add);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void subShort256VectorTestsBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.sub(b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, Short256VectorTests::sub);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void subShort256VectorTestsBroadcastMaskedSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.sub(b[i], vmask).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, mask, Short256VectorTests::sub);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void mulShort256VectorTestsBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.mul(b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, Short256VectorTests::mul);
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void mulShort256VectorTestsBroadcastMaskedSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.mul(b[i], vmask).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, mask, Short256VectorTests::mul);
    }




    @Test(dataProvider = "shortBinaryOpProvider")
    static void divShort256VectorTestsBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        replaceZero(b, (short) 1);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.div(b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, Short256VectorTests::div);
    }



    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void divShort256VectorTestsBroadcastMaskedSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        replaceZero(b, (short) 1);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.div(b[i], vmask).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, mask, Short256VectorTests::div);
    }



    @Test(dataProvider = "shortBinaryOpProvider")
    static void ORShort256VectorTestsBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.lanewise(VectorOperators.OR, b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, Short256VectorTests::OR);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void orShort256VectorTestsBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.or(b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, Short256VectorTests::or);
    }



    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void ORShort256VectorTestsBroadcastMaskedSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.lanewise(VectorOperators.OR, b[i], vmask).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, mask, Short256VectorTests::OR);
    }



    @Test(dataProvider = "shortBinaryOpProvider")
    static void ORShort256VectorTestsBroadcastLongSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.lanewise(VectorOperators.OR, (long)b[i]).intoArray(r, i);
        }

        assertBroadcastLongArraysEquals(a, b, r, Short256VectorTests::OR);
    }



    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void ORShort256VectorTestsBroadcastMaskedLongSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.lanewise(VectorOperators.OR, (long)b[i], vmask).intoArray(r, i);
        }

        assertBroadcastLongArraysEquals(a, b, r, mask, Short256VectorTests::OR);
    }






    static short LSHL(short a, short b) {
        return (short)((a << (b & 0xF)));
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void LSHLShort256VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.LSHL, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Short256VectorTests::LSHL);
    }



    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void LSHLShort256VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.LSHL, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, mask, Short256VectorTests::LSHL);
    }






    static short ASHR(short a, short b) {
        return (short)((a >> (b & 0xF)));
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void ASHRShort256VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.ASHR, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Short256VectorTests::ASHR);
    }



    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void ASHRShort256VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.ASHR, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, mask, Short256VectorTests::ASHR);
    }






    static short LSHR(short a, short b) {
        return (short)(((a & 0xFFFF) >>> (b & 0xF)));
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void LSHRShort256VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.LSHR, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Short256VectorTests::LSHR);
    }



    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void LSHRShort256VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.LSHR, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, mask, Short256VectorTests::LSHR);
    }






    static short LSHL_unary(short a, short b) {
        return (short)((a << (b & 15)));
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void LSHLShort256VectorTestsShift(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.LSHL, (int)b[i]).intoArray(r, i);
            }
        }

        assertShiftArraysEquals(a, b, r, Short256VectorTests::LSHL_unary);
    }



    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void LSHLShort256VectorTestsShift(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.LSHL, (int)b[i], vmask).intoArray(r, i);
            }
        }

        assertShiftArraysEquals(a, b, r, mask, Short256VectorTests::LSHL_unary);
    }






    static short LSHR_unary(short a, short b) {
        return (short)(((a & 0xFFFF) >>> (b & 15)));
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void LSHRShort256VectorTestsShift(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.LSHR, (int)b[i]).intoArray(r, i);
            }
        }

        assertShiftArraysEquals(a, b, r, Short256VectorTests::LSHR_unary);
    }



    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void LSHRShort256VectorTestsShift(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.LSHR, (int)b[i], vmask).intoArray(r, i);
            }
        }

        assertShiftArraysEquals(a, b, r, mask, Short256VectorTests::LSHR_unary);
    }






    static short ASHR_unary(short a, short b) {
        return (short)((a >> (b & 15)));
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void ASHRShort256VectorTestsShift(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.ASHR, (int)b[i]).intoArray(r, i);
            }
        }

        assertShiftArraysEquals(a, b, r, Short256VectorTests::ASHR_unary);
    }



    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void ASHRShort256VectorTestsShift(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.ASHR, (int)b[i], vmask).intoArray(r, i);
            }
        }

        assertShiftArraysEquals(a, b, r, mask, Short256VectorTests::ASHR_unary);
    }

    static short MIN(short a, short b) {
        return (short)(Math.min(a, b));
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void MINShort256VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.MIN, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Short256VectorTests::MIN);
    }
    static short min(short a, short b) {
        return (short)(Math.min(a, b));
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void minShort256VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
            av.min(bv).intoArray(r, i);
        }

        assertArraysEquals(a, b, r, Short256VectorTests::min);
    }
    static short MAX(short a, short b) {
        return (short)(Math.max(a, b));
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void MAXShort256VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.lanewise(VectorOperators.MAX, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Short256VectorTests::MAX);
    }
    static short max(short a, short b) {
        return (short)(Math.max(a, b));
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void maxShort256VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
            av.max(bv).intoArray(r, i);
        }

        assertArraysEquals(a, b, r, Short256VectorTests::max);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void MINShort256VectorTestsBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.lanewise(VectorOperators.MIN, b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, Short256VectorTests::MIN);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void minShort256VectorTestsBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.min(b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, Short256VectorTests::min);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void MAXShort256VectorTestsBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.lanewise(VectorOperators.MAX, b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, Short256VectorTests::MAX);
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void maxShort256VectorTestsBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.max(b[i]).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, r, Short256VectorTests::max);
    }

    static short AND(short[] a, int idx) {
        short res = -1;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res &= a[i];
        }

        return res;
    }

    static short AND(short[] a) {
        short res = -1;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            short tmp = -1;
            for (int j = 0; j < SPECIES.length(); j++) {
                tmp &= a[i + j];
            }
            res &= tmp;
        }

        return res;
    }


    @Test(dataProvider = "shortUnaryOpProvider")
    static void ANDShort256VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        short ra = -1;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.AND);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = -1;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ra &= av.reduceLanes(VectorOperators.AND);
            }
        }

        assertReductionArraysEquals(a, r, ra, Short256VectorTests::AND, Short256VectorTests::AND);
    }


    static short ANDMasked(short[] a, int idx, boolean[] mask) {
        short res = -1;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            if(mask[i % SPECIES.length()])
                res &= a[i];
        }

        return res;
    }

    static short ANDMasked(short[] a, boolean[] mask) {
        short res = -1;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            short tmp = -1;
            for (int j = 0; j < SPECIES.length(); j++) {
                if(mask[(i + j) % SPECIES.length()])
                    tmp &= a[i + j];
            }
            res &= tmp;
        }

        return res;
    }


    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void ANDShort256VectorTestsMasked(IntFunction<short[]> fa, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        short ra = -1;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.AND, vmask);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = -1;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ra &= av.reduceLanes(VectorOperators.AND, vmask);
            }
        }

        assertReductionArraysEqualsMasked(a, r, ra, mask, Short256VectorTests::ANDMasked, Short256VectorTests::ANDMasked);
    }


    static short OR(short[] a, int idx) {
        short res = 0;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res |= a[i];
        }

        return res;
    }

    static short OR(short[] a) {
        short res = 0;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            short tmp = 0;
            for (int j = 0; j < SPECIES.length(); j++) {
                tmp |= a[i + j];
            }
            res |= tmp;
        }

        return res;
    }


    @Test(dataProvider = "shortUnaryOpProvider")
    static void ORShort256VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        short ra = 0;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.OR);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = 0;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ra |= av.reduceLanes(VectorOperators.OR);
            }
        }

        assertReductionArraysEquals(a, r, ra, Short256VectorTests::OR, Short256VectorTests::OR);
    }


    static short ORMasked(short[] a, int idx, boolean[] mask) {
        short res = 0;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            if(mask[i % SPECIES.length()])
                res |= a[i];
        }

        return res;
    }

    static short ORMasked(short[] a, boolean[] mask) {
        short res = 0;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            short tmp = 0;
            for (int j = 0; j < SPECIES.length(); j++) {
                if(mask[(i + j) % SPECIES.length()])
                    tmp |= a[i + j];
            }
            res |= tmp;
        }

        return res;
    }


    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void ORShort256VectorTestsMasked(IntFunction<short[]> fa, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        short ra = 0;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.OR, vmask);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = 0;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ra |= av.reduceLanes(VectorOperators.OR, vmask);
            }
        }

        assertReductionArraysEqualsMasked(a, r, ra, mask, Short256VectorTests::ORMasked, Short256VectorTests::ORMasked);
    }


    static short XOR(short[] a, int idx) {
        short res = 0;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res ^= a[i];
        }

        return res;
    }

    static short XOR(short[] a) {
        short res = 0;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            short tmp = 0;
            for (int j = 0; j < SPECIES.length(); j++) {
                tmp ^= a[i + j];
            }
            res ^= tmp;
        }

        return res;
    }


    @Test(dataProvider = "shortUnaryOpProvider")
    static void XORShort256VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        short ra = 0;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.XOR);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = 0;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ra ^= av.reduceLanes(VectorOperators.XOR);
            }
        }

        assertReductionArraysEquals(a, r, ra, Short256VectorTests::XOR, Short256VectorTests::XOR);
    }


    static short XORMasked(short[] a, int idx, boolean[] mask) {
        short res = 0;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            if(mask[i % SPECIES.length()])
                res ^= a[i];
        }

        return res;
    }

    static short XORMasked(short[] a, boolean[] mask) {
        short res = 0;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            short tmp = 0;
            for (int j = 0; j < SPECIES.length(); j++) {
                if(mask[(i + j) % SPECIES.length()])
                    tmp ^= a[i + j];
            }
            res ^= tmp;
        }

        return res;
    }


    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void XORShort256VectorTestsMasked(IntFunction<short[]> fa, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        short ra = 0;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.XOR, vmask);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = 0;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ra ^= av.reduceLanes(VectorOperators.XOR, vmask);
            }
        }

        assertReductionArraysEqualsMasked(a, r, ra, mask, Short256VectorTests::XORMasked, Short256VectorTests::XORMasked);
    }

    static short ADD(short[] a, int idx) {
        short res = 0;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res += a[i];
        }

        return res;
    }

    static short ADD(short[] a) {
        short res = 0;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            short tmp = 0;
            for (int j = 0; j < SPECIES.length(); j++) {
                tmp += a[i + j];
            }
            res += tmp;
        }

        return res;
    }
    @Test(dataProvider = "shortUnaryOpProvider")
    static void ADDShort256VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        short ra = 0;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.ADD);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = 0;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ra += av.reduceLanes(VectorOperators.ADD);
            }
        }

        assertReductionArraysEquals(a, r, ra, Short256VectorTests::ADD, Short256VectorTests::ADD);
    }
    static short ADDMasked(short[] a, int idx, boolean[] mask) {
        short res = 0;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            if(mask[i % SPECIES.length()])
                res += a[i];
        }

        return res;
    }

    static short ADDMasked(short[] a, boolean[] mask) {
        short res = 0;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            short tmp = 0;
            for (int j = 0; j < SPECIES.length(); j++) {
                if(mask[(i + j) % SPECIES.length()])
                    tmp += a[i + j];
            }
            res += tmp;
        }

        return res;
    }
    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void ADDShort256VectorTestsMasked(IntFunction<short[]> fa, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        short ra = 0;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.ADD, vmask);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = 0;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ra += av.reduceLanes(VectorOperators.ADD, vmask);
            }
        }

        assertReductionArraysEqualsMasked(a, r, ra, mask, Short256VectorTests::ADDMasked, Short256VectorTests::ADDMasked);
    }
    static short MUL(short[] a, int idx) {
        short res = 1;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res *= a[i];
        }

        return res;
    }

    static short MUL(short[] a) {
        short res = 1;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            short tmp = 1;
            for (int j = 0; j < SPECIES.length(); j++) {
                tmp *= a[i + j];
            }
            res *= tmp;
        }

        return res;
    }
    @Test(dataProvider = "shortUnaryOpProvider")
    static void MULShort256VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        short ra = 1;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.MUL);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = 1;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ra *= av.reduceLanes(VectorOperators.MUL);
            }
        }

        assertReductionArraysEquals(a, r, ra, Short256VectorTests::MUL, Short256VectorTests::MUL);
    }
    static short MULMasked(short[] a, int idx, boolean[] mask) {
        short res = 1;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            if(mask[i % SPECIES.length()])
                res *= a[i];
        }

        return res;
    }

    static short MULMasked(short[] a, boolean[] mask) {
        short res = 1;
        for (int i = 0; i < a.length; i += SPECIES.length()) {
            short tmp = 1;
            for (int j = 0; j < SPECIES.length(); j++) {
                if(mask[(i + j) % SPECIES.length()])
                    tmp *= a[i + j];
            }
            res *= tmp;
        }

        return res;
    }
    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void MULShort256VectorTestsMasked(IntFunction<short[]> fa, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        short ra = 1;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.MUL, vmask);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = 1;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ra *= av.reduceLanes(VectorOperators.MUL, vmask);
            }
        }

        assertReductionArraysEqualsMasked(a, r, ra, mask, Short256VectorTests::MULMasked, Short256VectorTests::MULMasked);
    }
    static short MIN(short[] a, int idx) {
        short res = Short.MAX_VALUE;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res = (short)Math.min(res, a[i]);
        }

        return res;
    }

    static short MIN(short[] a) {
        short res = Short.MAX_VALUE;
        for (int i = 0; i < a.length; i++) {
            res = (short)Math.min(res, a[i]);
        }

        return res;
    }
    @Test(dataProvider = "shortUnaryOpProvider")
    static void MINShort256VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        short ra = Short.MAX_VALUE;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.MIN);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = Short.MAX_VALUE;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ra = (short)Math.min(ra, av.reduceLanes(VectorOperators.MIN));
            }
        }

        assertReductionArraysEquals(a, r, ra, Short256VectorTests::MIN, Short256VectorTests::MIN);
    }
    static short MINMasked(short[] a, int idx, boolean[] mask) {
        short res = Short.MAX_VALUE;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            if(mask[i % SPECIES.length()])
                res = (short)Math.min(res, a[i]);
        }

        return res;
    }

    static short MINMasked(short[] a, boolean[] mask) {
        short res = Short.MAX_VALUE;
        for (int i = 0; i < a.length; i++) {
            if(mask[i % SPECIES.length()])
                res = (short)Math.min(res, a[i]);
        }

        return res;
    }
    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void MINShort256VectorTestsMasked(IntFunction<short[]> fa, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        short ra = Short.MAX_VALUE;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.MIN, vmask);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = Short.MAX_VALUE;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ra = (short)Math.min(ra, av.reduceLanes(VectorOperators.MIN, vmask));
            }
        }

        assertReductionArraysEqualsMasked(a, r, ra, mask, Short256VectorTests::MINMasked, Short256VectorTests::MINMasked);
    }
    static short MAX(short[] a, int idx) {
        short res = Short.MIN_VALUE;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res = (short)Math.max(res, a[i]);
        }

        return res;
    }

    static short MAX(short[] a) {
        short res = Short.MIN_VALUE;
        for (int i = 0; i < a.length; i++) {
            res = (short)Math.max(res, a[i]);
        }

        return res;
    }
    @Test(dataProvider = "shortUnaryOpProvider")
    static void MAXShort256VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        short ra = Short.MIN_VALUE;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.MAX);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = Short.MIN_VALUE;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ra = (short)Math.max(ra, av.reduceLanes(VectorOperators.MAX));
            }
        }

        assertReductionArraysEquals(a, r, ra, Short256VectorTests::MAX, Short256VectorTests::MAX);
    }
    static short MAXMasked(short[] a, int idx, boolean[] mask) {
        short res = Short.MIN_VALUE;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            if(mask[i % SPECIES.length()])
                res = (short)Math.max(res, a[i]);
        }

        return res;
    }

    static short MAXMasked(short[] a, boolean[] mask) {
        short res = Short.MIN_VALUE;
        for (int i = 0; i < a.length; i++) {
            if(mask[i % SPECIES.length()])
                res = (short)Math.max(res, a[i]);
        }

        return res;
    }
    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void MAXShort256VectorTestsMasked(IntFunction<short[]> fa, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        short ra = Short.MIN_VALUE;

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                r[i] = av.reduceLanes(VectorOperators.MAX, vmask);
            }
        }

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            ra = Short.MIN_VALUE;
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ra = (short)Math.max(ra, av.reduceLanes(VectorOperators.MAX, vmask));
            }
        }

        assertReductionArraysEqualsMasked(a, r, ra, mask, Short256VectorTests::MAXMasked, Short256VectorTests::MAXMasked);
    }

    static boolean anyTrue(boolean[] a, int idx) {
        boolean res = false;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res |= a[i];
        }

        return res;
    }


    @Test(dataProvider = "boolUnaryOpProvider")
    static void anyTrueShort256VectorTests(IntFunction<boolean[]> fm) {
        boolean[] mask = fm.apply(SPECIES.length());
        boolean[] r = fmr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < mask.length; i += SPECIES.length()) {
                VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, i);
                r[i] = vmask.anyTrue();
            }
        }

        assertReductionBoolArraysEquals(mask, r, Short256VectorTests::anyTrue);
    }


    static boolean allTrue(boolean[] a, int idx) {
        boolean res = true;
        for (int i = idx; i < (idx + SPECIES.length()); i++) {
            res &= a[i];
        }

        return res;
    }


    @Test(dataProvider = "boolUnaryOpProvider")
    static void allTrueShort256VectorTests(IntFunction<boolean[]> fm) {
        boolean[] mask = fm.apply(SPECIES.length());
        boolean[] r = fmr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < mask.length; i += SPECIES.length()) {
                VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, i);
                r[i] = vmask.allTrue();
            }
        }

        assertReductionBoolArraysEquals(mask, r, Short256VectorTests::allTrue);
    }


    @Test(dataProvider = "shortUnaryOpProvider")
    static void withShort256VectorTests(IntFunction<short []> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.withLane(0, (short)4).intoArray(r, i);
            }
        }

        assertInsertArraysEquals(a, r, (short)4, 0);
    }
    static boolean testIS_DEFAULT(short a) {
        return bits(a)==0;
    }

    @Test(dataProvider = "shortTestOpProvider")
    static void IS_DEFAULTShort256VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                VectorMask<Short> mv = av.test(VectorOperators.IS_DEFAULT);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), testIS_DEFAULT(a[i + j]));
                }
            }
        }
    }

    static boolean testIS_NEGATIVE(short a) {
        return bits(a)<0;
    }

    @Test(dataProvider = "shortTestOpProvider")
    static void IS_NEGATIVEShort256VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                VectorMask<Short> mv = av.test(VectorOperators.IS_NEGATIVE);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), testIS_NEGATIVE(a[i + j]));
                }
            }
        }
    }





    @Test(dataProvider = "shortCompareOpProvider")
    static void LTShort256VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                VectorMask<Short> mv = av.compare(VectorOperators.LT, bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), a[i + j] < b[i + j]);
                }
            }
        }
    }


    @Test(dataProvider = "shortCompareOpProvider")
    static void ltShort256VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                VectorMask<Short> mv = av.lt(bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), a[i + j] < b[i + j]);
                }
            }
        }
    }

    @Test(dataProvider = "shortCompareOpMaskProvider")
    static void LTShort256VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                                IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                VectorMask<Short> mv = av.compare(VectorOperators.LT, bv, vmask);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), mask[j] && (a[i + j] < b[i + j]));
                }
            }
        }
    }


    @Test(dataProvider = "shortCompareOpProvider")
    static void GTShort256VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                VectorMask<Short> mv = av.compare(VectorOperators.GT, bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), a[i + j] > b[i + j]);
                }
            }
        }
    }

    @Test(dataProvider = "shortCompareOpMaskProvider")
    static void GTShort256VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                                IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                VectorMask<Short> mv = av.compare(VectorOperators.GT, bv, vmask);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), mask[j] && (a[i + j] > b[i + j]));
                }
            }
        }
    }


    @Test(dataProvider = "shortCompareOpProvider")
    static void EQShort256VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                VectorMask<Short> mv = av.compare(VectorOperators.EQ, bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), a[i + j] == b[i + j]);
                }
            }
        }
    }


    @Test(dataProvider = "shortCompareOpProvider")
    static void eqShort256VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                VectorMask<Short> mv = av.eq(bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), a[i + j] == b[i + j]);
                }
            }
        }
    }

    @Test(dataProvider = "shortCompareOpMaskProvider")
    static void EQShort256VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                                IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                VectorMask<Short> mv = av.compare(VectorOperators.EQ, bv, vmask);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), mask[j] && (a[i + j] == b[i + j]));
                }
            }
        }
    }


    @Test(dataProvider = "shortCompareOpProvider")
    static void NEShort256VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                VectorMask<Short> mv = av.compare(VectorOperators.NE, bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), a[i + j] != b[i + j]);
                }
            }
        }
    }

    @Test(dataProvider = "shortCompareOpMaskProvider")
    static void NEShort256VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                                IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                VectorMask<Short> mv = av.compare(VectorOperators.NE, bv, vmask);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), mask[j] && (a[i + j] != b[i + j]));
                }
            }
        }
    }


    @Test(dataProvider = "shortCompareOpProvider")
    static void LEShort256VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                VectorMask<Short> mv = av.compare(VectorOperators.LE, bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), a[i + j] <= b[i + j]);
                }
            }
        }
    }

    @Test(dataProvider = "shortCompareOpMaskProvider")
    static void LEShort256VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                                IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                VectorMask<Short> mv = av.compare(VectorOperators.LE, bv, vmask);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), mask[j] && (a[i + j] <= b[i + j]));
                }
            }
        }
    }


    @Test(dataProvider = "shortCompareOpProvider")
    static void GEShort256VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                VectorMask<Short> mv = av.compare(VectorOperators.GE, bv);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), a[i + j] >= b[i + j]);
                }
            }
        }
    }

    @Test(dataProvider = "shortCompareOpMaskProvider")
    static void GEShort256VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                                IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                VectorMask<Short> mv = av.compare(VectorOperators.GE, bv, vmask);

                // Check results as part of computation.
                for (int j = 0; j < SPECIES.length(); j++) {
                    Assert.assertEquals(mv.laneIsSet(j), mask[j] && (a[i + j] >= b[i + j]));
                }
            }
        }
    }


    @Test(dataProvider = "shortCompareOpProvider")
    static void LTShort256VectorTestsBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            VectorMask<Short> mv = av.compare(VectorOperators.LT, b[i]);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j), a[i + j] < b[i]);
            }
        }
    }


    @Test(dataProvider = "shortCompareOpMaskProvider")
    static void LTShort256VectorTestsBroadcastMaskedSmokeTest(IntFunction<short[]> fa,
                                IntFunction<short[]> fb, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            VectorMask<Short> mv = av.compare(VectorOperators.LT, b[i], vmask);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j), mask[j] && (a[i + j] < b[i]));
            }
        }
    }

    @Test(dataProvider = "shortCompareOpProvider")
    static void LTShort256VectorTestsBroadcastLongSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            VectorMask<Short> mv = av.compare(VectorOperators.LT, (long)b[i]);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j), a[i + j] < (short)((long)b[i]));
            }
        }
    }


    @Test(dataProvider = "shortCompareOpMaskProvider")
    static void LTShort256VectorTestsBroadcastLongMaskedSmokeTest(IntFunction<short[]> fa,
                                IntFunction<short[]> fb, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            VectorMask<Short> mv = av.compare(VectorOperators.LT, (long)b[i], vmask);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j), mask[j] && (a[i + j] < (short)((long)b[i])));
            }
        }
    }

    @Test(dataProvider = "shortCompareOpProvider")
    static void EQShort256VectorTestsBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            VectorMask<Short> mv = av.compare(VectorOperators.EQ, b[i]);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j), a[i + j] == b[i]);
            }
        }
    }


    @Test(dataProvider = "shortCompareOpMaskProvider")
    static void EQShort256VectorTestsBroadcastMaskedSmokeTest(IntFunction<short[]> fa,
                                IntFunction<short[]> fb, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            VectorMask<Short> mv = av.compare(VectorOperators.EQ, b[i], vmask);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j), mask[j] && (a[i + j] == b[i]));
            }
        }
    }

    @Test(dataProvider = "shortCompareOpProvider")
    static void EQShort256VectorTestsBroadcastLongSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            VectorMask<Short> mv = av.compare(VectorOperators.EQ, (long)b[i]);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j), a[i + j] == (short)((long)b[i]));
            }
        }
    }


    @Test(dataProvider = "shortCompareOpMaskProvider")
    static void EQShort256VectorTestsBroadcastLongMaskedSmokeTest(IntFunction<short[]> fa,
                                IntFunction<short[]> fb, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());

        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            VectorMask<Short> mv = av.compare(VectorOperators.EQ, (long)b[i], vmask);

            // Check results as part of computation.
            for (int j = 0; j < SPECIES.length(); j++) {
                Assert.assertEquals(mv.laneIsSet(j), mask[j] && (a[i + j] == (short)((long)b[i])));
            }
        }
    }

    static short blend(short a, short b, boolean mask) {
        return mask ? b : a;
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void blendShort256VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.blend(bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, mask, Short256VectorTests::blend);
    }

    @Test(dataProvider = "shortUnaryOpShuffleProvider")
    static void RearrangeShort256VectorTests(IntFunction<short[]> fa,
                                           BiFunction<Integer,Integer,int[]> fs) {
        short[] a = fa.apply(SPECIES.length());
        int[] order = fs.apply(a.length, SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.rearrange(VectorShuffle.fromArray(SPECIES, order, i)).intoArray(r, i);
            }
        }

        assertRearrangeArraysEquals(a, r, order, SPECIES.length());
    }




    @Test(dataProvider = "shortUnaryOpProvider")
    static void getShort256VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                int num_lanes = SPECIES.length();
                // Manually unroll because full unroll happens after intrinsification.
                // Unroll is needed because get intrinsic requires for index to be a known constant.
                if (num_lanes == 1) {
                    r[i]=av.lane(0);
                } else if (num_lanes == 2) {
                    r[i]=av.lane(0);
                    r[i+1]=av.lane(1);
                } else if (num_lanes == 4) {
                    r[i]=av.lane(0);
                    r[i+1]=av.lane(1);
                    r[i+2]=av.lane(2);
                    r[i+3]=av.lane(3);
                } else if (num_lanes == 8) {
                    r[i]=av.lane(0);
                    r[i+1]=av.lane(1);
                    r[i+2]=av.lane(2);
                    r[i+3]=av.lane(3);
                    r[i+4]=av.lane(4);
                    r[i+5]=av.lane(5);
                    r[i+6]=av.lane(6);
                    r[i+7]=av.lane(7);
                } else if (num_lanes == 16) {
                    r[i]=av.lane(0);
                    r[i+1]=av.lane(1);
                    r[i+2]=av.lane(2);
                    r[i+3]=av.lane(3);
                    r[i+4]=av.lane(4);
                    r[i+5]=av.lane(5);
                    r[i+6]=av.lane(6);
                    r[i+7]=av.lane(7);
                    r[i+8]=av.lane(8);
                    r[i+9]=av.lane(9);
                    r[i+10]=av.lane(10);
                    r[i+11]=av.lane(11);
                    r[i+12]=av.lane(12);
                    r[i+13]=av.lane(13);
                    r[i+14]=av.lane(14);
                    r[i+15]=av.lane(15);
                } else if (num_lanes == 32) {
                    r[i]=av.lane(0);
                    r[i+1]=av.lane(1);
                    r[i+2]=av.lane(2);
                    r[i+3]=av.lane(3);
                    r[i+4]=av.lane(4);
                    r[i+5]=av.lane(5);
                    r[i+6]=av.lane(6);
                    r[i+7]=av.lane(7);
                    r[i+8]=av.lane(8);
                    r[i+9]=av.lane(9);
                    r[i+10]=av.lane(10);
                    r[i+11]=av.lane(11);
                    r[i+12]=av.lane(12);
                    r[i+13]=av.lane(13);
                    r[i+14]=av.lane(14);
                    r[i+15]=av.lane(15);
                    r[i+16]=av.lane(16);
                    r[i+17]=av.lane(17);
                    r[i+18]=av.lane(18);
                    r[i+19]=av.lane(19);
                    r[i+20]=av.lane(20);
                    r[i+21]=av.lane(21);
                    r[i+22]=av.lane(22);
                    r[i+23]=av.lane(23);
                    r[i+24]=av.lane(24);
                    r[i+25]=av.lane(25);
                    r[i+26]=av.lane(26);
                    r[i+27]=av.lane(27);
                    r[i+28]=av.lane(28);
                    r[i+29]=av.lane(29);
                    r[i+30]=av.lane(30);
                    r[i+31]=av.lane(31);
                } else if (num_lanes == 64) {
                    r[i]=av.lane(0);
                    r[i+1]=av.lane(1);
                    r[i+2]=av.lane(2);
                    r[i+3]=av.lane(3);
                    r[i+4]=av.lane(4);
                    r[i+5]=av.lane(5);
                    r[i+6]=av.lane(6);
                    r[i+7]=av.lane(7);
                    r[i+8]=av.lane(8);
                    r[i+9]=av.lane(9);
                    r[i+10]=av.lane(10);
                    r[i+11]=av.lane(11);
                    r[i+12]=av.lane(12);
                    r[i+13]=av.lane(13);
                    r[i+14]=av.lane(14);
                    r[i+15]=av.lane(15);
                    r[i+16]=av.lane(16);
                    r[i+17]=av.lane(17);
                    r[i+18]=av.lane(18);
                    r[i+19]=av.lane(19);
                    r[i+20]=av.lane(20);
                    r[i+21]=av.lane(21);
                    r[i+22]=av.lane(22);
                    r[i+23]=av.lane(23);
                    r[i+24]=av.lane(24);
                    r[i+25]=av.lane(25);
                    r[i+26]=av.lane(26);
                    r[i+27]=av.lane(27);
                    r[i+28]=av.lane(28);
                    r[i+29]=av.lane(29);
                    r[i+30]=av.lane(30);
                    r[i+31]=av.lane(31);
                    r[i+32]=av.lane(32);
                    r[i+33]=av.lane(33);
                    r[i+34]=av.lane(34);
                    r[i+35]=av.lane(35);
                    r[i+36]=av.lane(36);
                    r[i+37]=av.lane(37);
                    r[i+38]=av.lane(38);
                    r[i+39]=av.lane(39);
                    r[i+40]=av.lane(40);
                    r[i+41]=av.lane(41);
                    r[i+42]=av.lane(42);
                    r[i+43]=av.lane(43);
                    r[i+44]=av.lane(44);
                    r[i+45]=av.lane(45);
                    r[i+46]=av.lane(46);
                    r[i+47]=av.lane(47);
                    r[i+48]=av.lane(48);
                    r[i+49]=av.lane(49);
                    r[i+50]=av.lane(50);
                    r[i+51]=av.lane(51);
                    r[i+52]=av.lane(52);
                    r[i+53]=av.lane(53);
                    r[i+54]=av.lane(54);
                    r[i+55]=av.lane(55);
                    r[i+56]=av.lane(56);
                    r[i+57]=av.lane(57);
                    r[i+58]=av.lane(58);
                    r[i+59]=av.lane(59);
                    r[i+60]=av.lane(60);
                    r[i+61]=av.lane(61);
                    r[i+62]=av.lane(62);
                    r[i+63]=av.lane(63);
                } else {
                    for (int j = 0; j < SPECIES.length(); j++) {
                        r[i+j]=av.lane(j);
                    }
                }
            }
        }

        assertArraysEquals(a, r, Short256VectorTests::get);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void BroadcastShort256VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = new short[a.length];

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector.broadcast(SPECIES, a[i]).intoArray(r, i);
            }
        }

        assertBroadcastArraysEquals(a, r);
    }





    @Test(dataProvider = "shortUnaryOpProvider")
    static void ZeroShort256VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = new short[a.length];

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector.zero(SPECIES).intoArray(a, i);
            }
        }

        Assert.assertEquals(a, r);
    }




    static short[] sliceUnary(short[] a, int origin, int idx) {
        short[] res = new short[SPECIES.length()];
        for (int i = 0; i < SPECIES.length(); i++){
            if(i+origin < SPECIES.length())
                res[i] = a[idx+i+origin];
            else
                res[i] = (short)0;
        }
        return res;
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void sliceUnaryShort256VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = new short[a.length];
        int origin = (new java.util.Random()).nextInt(SPECIES.length());
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.slice(origin).intoArray(r, i);
            }
        }

        assertArraysEquals(a, r, origin, Short256VectorTests::sliceUnary);
    }
    static short[] sliceBinary(short[] a, short[] b, int origin, int idx) {
        short[] res = new short[SPECIES.length()];
        for (int i = 0, j = 0; i < SPECIES.length(); i++){
            if(i+origin < SPECIES.length())
                res[i] = a[idx+i+origin];
            else {
                res[i] = b[idx+j];
                j++;
            }
        }
        return res;
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void sliceBinaryShort256VectorTestsBinary(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = new short[a.length];
        int origin = (new java.util.Random()).nextInt(SPECIES.length());
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.slice(origin, bv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, origin, Short256VectorTests::sliceBinary);
    }
    static short[] slice(short[] a, short[] b, int origin, boolean[] mask, int idx) {
        short[] res = new short[SPECIES.length()];
        for (int i = 0, j = 0; i < SPECIES.length(); i++){
            if(i+origin < SPECIES.length())
                res[i] = mask[i] ? a[idx+i+origin] : (short)0;
            else {
                res[i] = mask[i] ? b[idx+j] : (short)0;
                j++;
            }
        }
        return res;
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void sliceShort256VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
    IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        short[] r = new short[a.length];
        int origin = (new java.util.Random()).nextInt(SPECIES.length());
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.slice(origin, bv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, origin, mask, Short256VectorTests::slice);
    }
    static short[] unsliceUnary(short[] a, int origin, int idx) {
        short[] res = new short[SPECIES.length()];
        for (int i = 0, j = 0; i < SPECIES.length(); i++){
            if(i < origin)
                res[i] = (short)0;
            else {
                res[i] = a[idx+j];
                j++;
            }
        }
        return res;
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void unsliceUnaryShort256VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = new short[a.length];
        int origin = (new java.util.Random()).nextInt(SPECIES.length());
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.unslice(origin).intoArray(r, i);
            }
        }

        assertArraysEquals(a, r, origin, Short256VectorTests::unsliceUnary);
    }
    static short[] unsliceBinary(short[] a, short[] b, int origin, int part, int idx) {
        short[] res = new short[SPECIES.length()];
        for (int i = 0, j = 0; i < SPECIES.length(); i++){
            if (part == 0) {
                if (i < origin)
                    res[i] = b[idx+i];
                else {
                    res[i] = a[idx+j];
                    j++;
                }
            } else if (part == 1) {
                if (i < origin)
                    res[i] = a[idx+SPECIES.length()-origin+i];
                else {
                    res[i] = b[idx+origin+j];
                    j++;
                }
            }
        }
        return res;
    }

    @Test(dataProvider = "shortBinaryOpProvider")
    static void unsliceBinaryShort256VectorTestsBinary(IntFunction<short[]> fa, IntFunction<short[]> fb) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] r = new short[a.length];
        int origin = (new java.util.Random()).nextInt(SPECIES.length());
        int part = (new java.util.Random()).nextInt(2);
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.unslice(origin, bv, part).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, origin, part, Short256VectorTests::unsliceBinary);
    }
    static short[] unslice(short[] a, short[] b, int origin, int part, boolean[] mask, int idx) {
        short[] res = new short[SPECIES.length()];
        for (int i = 0, j = 0; i < SPECIES.length(); i++){
            if(i+origin < SPECIES.length())
                res[i] = b[idx+i+origin];
            else {
                res[i] = b[idx+j];
                j++;
            }
        }
        for (int i = 0; i < SPECIES.length(); i++){
            res[i] = mask[i] ? a[idx+i] : res[i];
        }
        short[] res1 = new short[SPECIES.length()];
        if (part == 0) {
            for (int i = 0, j = 0; i < SPECIES.length(); i++){
                if (i < origin)
                    res1[i] = b[idx+i];
                else {
                   res1[i] = res[j];
                   j++;
                }
            }
        } else if (part == 1) {
            for (int i = 0, j = 0; i < SPECIES.length(); i++){
                if (i < origin)
                    res1[i] = res[SPECIES.length()-origin+i];
                else {
                    res1[i] = b[idx+origin+j];
                    j++;
                }
            }
        }
        return res1;
    }

    @Test(dataProvider = "shortBinaryOpMaskProvider")
    static void unsliceShort256VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
    IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);
        short[] r = new short[a.length];
        int origin = (new java.util.Random()).nextInt(SPECIES.length());
        int part = (new java.util.Random()).nextInt(2);
        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                av.unslice(origin, bv, part, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, origin, part, mask, Short256VectorTests::unslice);
    }























    static short BITWISE_BLEND(short a, short b, short c) {
        return (short)((a&~(c))|(b&c));
    }
    static short bitwiseBlend(short a, short b, short c) {
        return (short)((a&~(c))|(b&c));
    }


    @Test(dataProvider = "shortTernaryOpProvider")
    static void BITWISE_BLENDShort256VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb, IntFunction<short[]> fc) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] c = fc.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                ShortVector cv = ShortVector.fromArray(SPECIES, c, i);
                av.lanewise(VectorOperators.BITWISE_BLEND, bv, cv).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, c, r, Short256VectorTests::BITWISE_BLEND);
    }
    @Test(dataProvider = "shortTernaryOpProvider")
    static void bitwiseBlendShort256VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb, IntFunction<short[]> fc) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] c = fc.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
            ShortVector cv = ShortVector.fromArray(SPECIES, c, i);
            av.bitwiseBlend(bv, cv).intoArray(r, i);
        }

        assertArraysEquals(a, b, c, r, Short256VectorTests::bitwiseBlend);
    }


    @Test(dataProvider = "shortTernaryOpMaskProvider")
    static void BITWISE_BLENDShort256VectorTestsMasked(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<short[]> fc, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] c = fc.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
                ShortVector cv = ShortVector.fromArray(SPECIES, c, i);
                av.lanewise(VectorOperators.BITWISE_BLEND, bv, cv, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, c, r, mask, Short256VectorTests::BITWISE_BLEND);
    }




    @Test(dataProvider = "shortTernaryOpProvider")
    static void BITWISE_BLENDShort256VectorTestsBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb, IntFunction<short[]> fc) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] c = fc.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
            av.lanewise(VectorOperators.BITWISE_BLEND, bv, c[i]).intoArray(r, i);
        }
        assertBroadcastArraysEquals(a, b, c, r, Short256VectorTests::BITWISE_BLEND);
    }

    @Test(dataProvider = "shortTernaryOpProvider")
    static void BITWISE_BLENDShort256VectorTestsAltBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb, IntFunction<short[]> fc) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] c = fc.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            ShortVector cv = ShortVector.fromArray(SPECIES, c, i);
            av.lanewise(VectorOperators.BITWISE_BLEND, b[i], cv).intoArray(r, i);
        }
        assertAltBroadcastArraysEquals(a, b, c, r, Short256VectorTests::BITWISE_BLEND);
    }
    @Test(dataProvider = "shortTernaryOpProvider")
    static void bitwiseBlendShort256VectorTestsBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb, IntFunction<short[]> fc) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] c = fc.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
            av.bitwiseBlend(bv, c[i]).intoArray(r, i);
        }
        assertBroadcastArraysEquals(a, b, c, r, Short256VectorTests::bitwiseBlend);
    }

    @Test(dataProvider = "shortTernaryOpProvider")
    static void bitwiseBlendShort256VectorTestsAltBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb, IntFunction<short[]> fc) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] c = fc.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            ShortVector cv = ShortVector.fromArray(SPECIES, c, i);
            av.bitwiseBlend(b[i], cv).intoArray(r, i);
        }
        assertAltBroadcastArraysEquals(a, b, c, r, Short256VectorTests::bitwiseBlend);
    }


    @Test(dataProvider = "shortTernaryOpMaskProvider")
    static void BITWISE_BLENDShort256VectorTestsBroadcastMaskedSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<short[]> fc, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] c = fc.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            ShortVector bv = ShortVector.fromArray(SPECIES, b, i);
            av.lanewise(VectorOperators.BITWISE_BLEND, bv, c[i], vmask).intoArray(r, i);
        }

        assertBroadcastArraysEquals(a, b, c, r, mask, Short256VectorTests::BITWISE_BLEND);
    }

    @Test(dataProvider = "shortTernaryOpMaskProvider")
    static void BITWISE_BLENDShort256VectorTestsAltBroadcastMaskedSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<short[]> fc, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] c = fc.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            ShortVector cv = ShortVector.fromArray(SPECIES, c, i);
            av.lanewise(VectorOperators.BITWISE_BLEND, b[i], cv, vmask).intoArray(r, i);
        }

        assertAltBroadcastArraysEquals(a, b, c, r, mask, Short256VectorTests::BITWISE_BLEND);
    }




    @Test(dataProvider = "shortTernaryOpProvider")
    static void BITWISE_BLENDShort256VectorTestsDoubleBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb, IntFunction<short[]> fc) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] c = fc.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.lanewise(VectorOperators.BITWISE_BLEND, b[i], c[i]).intoArray(r, i);
        }

        assertDoubleBroadcastArraysEquals(a, b, c, r, Short256VectorTests::BITWISE_BLEND);
    }
    @Test(dataProvider = "shortTernaryOpProvider")
    static void bitwiseBlendShort256VectorTestsDoubleBroadcastSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb, IntFunction<short[]> fc) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] c = fc.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.bitwiseBlend(b[i], c[i]).intoArray(r, i);
        }

        assertDoubleBroadcastArraysEquals(a, b, c, r, Short256VectorTests::bitwiseBlend);
    }


    @Test(dataProvider = "shortTernaryOpMaskProvider")
    static void BITWISE_BLENDShort256VectorTestsDoubleBroadcastMaskedSmokeTest(IntFunction<short[]> fa, IntFunction<short[]> fb,
                                          IntFunction<short[]> fc, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] b = fb.apply(SPECIES.length());
        short[] c = fc.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int i = 0; i < a.length; i += SPECIES.length()) {
            ShortVector av = ShortVector.fromArray(SPECIES, a, i);
            av.lanewise(VectorOperators.BITWISE_BLEND, b[i], c[i], vmask).intoArray(r, i);
        }

        assertDoubleBroadcastArraysEquals(a, b, c, r, mask, Short256VectorTests::BITWISE_BLEND);
    }


    static short NEG(short a) {
        return (short)(-((short)a));
    }

    static short neg(short a) {
        return (short)(-((short)a));
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void NEGShort256VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.NEG).intoArray(r, i);
            }
        }

        assertArraysEquals(a, r, Short256VectorTests::NEG);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void negShort256VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.neg().intoArray(r, i);
            }
        }

        assertArraysEquals(a, r, Short256VectorTests::neg);
    }

    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void NEGMaskedShort256VectorTests(IntFunction<short[]> fa,
                                                IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.NEG, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, r, mask, Short256VectorTests::NEG);
    }

    static short ABS(short a) {
        return (short)(Math.abs((short)a));
    }

    static short abs(short a) {
        return (short)(Math.abs((short)a));
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void ABSShort256VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.ABS).intoArray(r, i);
            }
        }

        assertArraysEquals(a, r, Short256VectorTests::ABS);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void absShort256VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.abs().intoArray(r, i);
            }
        }

        assertArraysEquals(a, r, Short256VectorTests::abs);
    }

    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void ABSMaskedShort256VectorTests(IntFunction<short[]> fa,
                                                IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.ABS, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, r, mask, Short256VectorTests::ABS);
    }


    static short NOT(short a) {
        return (short)(~((short)a));
    }

    static short not(short a) {
        return (short)(~((short)a));
    }



    @Test(dataProvider = "shortUnaryOpProvider")
    static void NOTShort256VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.NOT).intoArray(r, i);
            }
        }

        assertArraysEquals(a, r, Short256VectorTests::NOT);
    }

    @Test(dataProvider = "shortUnaryOpProvider")
    static void notShort256VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.not().intoArray(r, i);
            }
        }

        assertArraysEquals(a, r, Short256VectorTests::not);
    }



    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void NOTMaskedShort256VectorTests(IntFunction<short[]> fa,
                                                IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.NOT, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, r, mask, Short256VectorTests::NOT);
    }



    static short ZOMO(short a) {
        return (short)((a==0?0:-1));
    }



    @Test(dataProvider = "shortUnaryOpProvider")
    static void ZOMOShort256VectorTests(IntFunction<short[]> fa) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.ZOMO).intoArray(r, i);
            }
        }

        assertArraysEquals(a, r, Short256VectorTests::ZOMO);
    }



    @Test(dataProvider = "shortUnaryOpMaskProvider")
    static void ZOMOMaskedShort256VectorTests(IntFunction<short[]> fa,
                                                IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        short[] r = fr.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.lanewise(VectorOperators.ZOMO, vmask).intoArray(r, i);
            }
        }

        assertArraysEquals(a, r, mask, Short256VectorTests::ZOMO);
    }




    static short[] gather(short a[], int ix, int[] b, int iy) {
        short[] res = new short[SPECIES.length()];
        for (int i = 0; i < SPECIES.length(); i++) {
            int bi = iy + i;
            res[i] = a[b[bi] + ix];
        }
        return res;
    }

    @Test(dataProvider = "shortUnaryOpIndexProvider")
    static void gatherShort256VectorTests(IntFunction<short[]> fa, BiFunction<Integer,Integer,int[]> fs) {
        short[] a = fa.apply(SPECIES.length());
        int[] b    = fs.apply(a.length, SPECIES.length());
        short[] r = new short[a.length];

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i, b, i);
                av.intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, Short256VectorTests::gather);
    }
    static short[] gatherMasked(short a[], int ix, boolean[] mask, int[] b, int iy) {
        short[] res = new short[SPECIES.length()];
        for (int i = 0; i < SPECIES.length(); i++) {
            int bi = iy + i;
            if (mask[i]) {
              res[i] = a[b[bi] + ix];
            }
        }
        return res;
    }

    @Test(dataProvider = "shortUnaryMaskedOpIndexProvider")
    static void gatherMaskedShort256VectorTests(IntFunction<short[]> fa, BiFunction<Integer,Integer,int[]> fs, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        int[] b    = fs.apply(a.length, SPECIES.length());
        short[] r = new short[a.length];
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i, b, i, vmask);
                av.intoArray(r, i);
            }
        }

        assertArraysEquals(a, b, r, mask, Short256VectorTests::gatherMasked);
    }

    static short[] scatter(short a[], int ix, int[] b, int iy) {
      short[] res = new short[SPECIES.length()];
      for (int i = 0; i < SPECIES.length(); i++) {
        int bi = iy + i;
        res[b[bi]] = a[i + ix];
      }
      return res;
    }

    @Test(dataProvider = "shortUnaryOpIndexProvider")
    static void scatterShort256VectorTests(IntFunction<short[]> fa, BiFunction<Integer,Integer,int[]> fs) {
        short[] a = fa.apply(SPECIES.length());
        int[] b = fs.apply(a.length, SPECIES.length());
        short[] r = new short[a.length];

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.intoArray(r, i, b, i);
            }
        }

        assertArraysEquals(a, b, r, Short256VectorTests::scatter);
    }

    static short[] scatterMasked(short r[], short a[], int ix, boolean[] mask, int[] b, int iy) {
      // First, gather r.
      short[] oldVal = gather(r, ix, b, iy);
      short[] newVal = new short[SPECIES.length()];

      // Second, blending it with a.
      for (int i = 0; i < SPECIES.length(); i++) {
        newVal[i] = blend(oldVal[i], a[i+ix], mask[i]);
      }

      // Third, scatter: copy old value of r, and scatter it manually.
      short[] res = Arrays.copyOfRange(r, ix, ix+SPECIES.length());
      for (int i = 0; i < SPECIES.length(); i++) {
        int bi = iy + i;
        res[b[bi]] = newVal[i];
      }

      return res;
    }

    @Test(dataProvider = "scatterMaskedOpIndexProvider")
    static void scatterMaskedShort256VectorTests(IntFunction<short[]> fa, IntFunction<short[]> fb, BiFunction<Integer,Integer,int[]> fs, IntFunction<boolean[]> fm) {
        short[] a = fa.apply(SPECIES.length());
        int[] b = fs.apply(a.length, SPECIES.length());
        short[] r = fb.apply(SPECIES.length());
        boolean[] mask = fm.apply(SPECIES.length());
        VectorMask<Short> vmask = VectorMask.fromArray(SPECIES, mask, 0);

        for (int ic = 0; ic < INVOC_COUNT; ic++) {
            for (int i = 0; i < a.length; i += SPECIES.length()) {
                ShortVector av = ShortVector.fromArray(SPECIES, a, i);
                av.intoArray(r, i, b, i, vmask);
            }
        }

        assertArraysEquals(a, b, r, mask, Short256VectorTests::scatterMasked);
    }

}

