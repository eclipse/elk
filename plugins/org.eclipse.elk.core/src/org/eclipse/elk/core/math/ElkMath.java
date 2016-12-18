/*******************************************************************************
 * Copyright (c) 2009, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.math;

import java.util.ListIterator;

/**
 * Mathematics utility class for the Eclipse Layout Kernel.
 * 
 * @author msp
 * @kieler.design 2014-04-17 reviewed by cds, chsch, tit, uru
 * @kieler.rating 2009-12-11 proposed yellow msp
 */
public final class ElkMath {

    /**
     * Hidden constructor to avoid instantiation.
     */
    private ElkMath() {
    }

    /** table of precomputed factorial values. */
    private static final long[] FACT_TABLE = { 1L, 1L, 2L, 6L, 24L, 120L, 720L, 5040L, 40320L,
            362880L, 3628800L, 39916800L, 479001600L, 6227020800L, 87178291200L, 1307674368000L,
            20922789888000L, 355687428096000L, 6402373705728000L, 121645100408832000L,
            2432902008176640000L };

    /**
     * The factorial of an integer x as long value. If x is negative or greater then 20 then
     * IllegalArgumentException. This method always returns the exact value for x between 0 and 20.
     * 
     * @param x
     *            an integer
     * @return the factorial of x
     * @throws IllegalArgumentException
     *             if x<0 or x>20
     */
    public static long factl(final int x) {
        // IllegalArgumentException when x not between 0 and 20
        if (x < 0 || x >= FACT_TABLE.length) {
            throw new IllegalArgumentException("The input must be between 0 and "
                    + FACT_TABLE.length);
        }
        // return the appropriate value from FACT_TABLE with index x
        return FACT_TABLE[x];

    }

    /**
     * The factorial of an integer x as double value. If x is negative then
     * IllegalArgumentException. If x>26 then the result is Infinity. This method returns the exact
     * value for small input values, and uses Stirling's approximation for large input values.
     * 
     * @param x
     *            an integer
     * @return the factorial of x
     * @throws IllegalArgumentException
     *             if x<0
     */
    public static double factd(final int x) {
        // IllegalArgumentException when x < 0
        if (x < 0) {
            throw new IllegalArgumentException("The input must be positive");
        } else if (x < FACT_TABLE.length) {
            return FACT_TABLE[x];
        } else {
            return Math.sqrt(2.0 * Math.PI * x) * (powf(x, x) / powd(Math.E, x));
        }
    }

    /**
     * The binomial coefficient of integers n and k as long value. If n is not positive or k is not
     * between 0 and n the result is IllegalArgumentException. This method always returns the exact
     * value, but may take very long for large input values.
     * 
     * @param n
     *            the upper integer
     * @param k
     *            the lower integer
     * @return n choose k
     * @throws IllegalArgumentException
     *             if n < 0 or n < 0 or k > n
     */
    public static long binomiall(final int n, final int k) {
        if (n < 0 || k < 0) {
            throw new IllegalArgumentException("k and n must be positive");
        } else if (k > n) {
            throw new IllegalArgumentException("k must be smaller than n");
        } else if (k == 0 || k == n) {
            return 1;
        } else if (n == 0) {
            return 0;
        } else if (n < FACT_TABLE.length) {
            return factl(n) / (factl(k) * factl(n - k));
        } else {
            return binomiall(n - 1, k - 1) + binomiall(n - 1, k);
        }
    }

    /**
     * The binomial coefficient of integers n and k as double value. If n is not positive or k is
     * not between 0 and n the result is IllegalArgumentException. This method returns the exact
     * value for small input values, and uses an approximation for large input values.
     * 
     * @param n
     *            the upper integer
     * @param k
     *            the lower integer
     * @return n choose k
     * @throws IllegalArgumentException
     *             if n < 0 or n < 0 or k > n
     */
    public static double binomiald(final int n, final int k) {
        if (n < 0 || k < 0) {
            throw new IllegalArgumentException("k and n must be positive");
        } else if (k > n) {
            throw new IllegalArgumentException("k must be smaller than n");
        } else if (k == 0 || k == n) {
            return 1;
        } else if (n == 0) {
            return 0;
        } else {
            return factd(n) / (factd(k) * factd(n - k));
        }
    }

    /**
     * The first argument raised to the power of the second argument.
     * 
     * @param a
     *            the base
     * @param b
     *            the exponent
     * @return a to the power of b
     */
    public static double powd(final double a, final int b) {
        double result = 1.0;
        double base = a;
        int exp = (b >= 0 ? b : -b);
        while (exp > 0) {
            if (exp % 2 == 0) {
                base *= base;
                exp /= 2;
            } else {
                result *= base;
                exp -= 1;
            }
        }
        if (b < 0) {
            return 1.0 / result;
        } else {
            return result;
        }
    }

    /**
     * The first argument raised to the power of the second argument.
     * 
     * @param a
     *            the base
     * @param b
     *            the exponent
     * @return a to the power of b
     */
    public static float powf(final float a, final int b) {
        float result = 1.0f;
        float base = a;
        int exp = (b >= 0 ? b : -b);
        while (exp > 0) {
            if (exp % 2 == 0) {
                base *= base;
                exp /= 2;
            } else {
                result *= base;
                exp -= 1;
            }
        }
        if (b < 0) {
            return 1.0f / result;
        } else {
            return result;
        }
    }

    /**
     * Compute a number of approximation points on the Bezier curve defined by the given control
     * points. The degree of the curve is derived from the number of control points. The array of
     * resulting curve points includes the target point, but does not include the source point of
     * the curve.
     * 
     * @param controlPoints the control points
     * @param resultSize number of returned curve points
     * @return points on the curve defined by the given control points
     */
    public static KVector[] approximateBezierSegment(final int resultSize,
            final KVector... controlPoints) {
        if (resultSize <= 0) {
            return new KVector[0];
        }
        KVector[] result = new KVector[resultSize];
        int n = controlPoints.length - 1;
        double dt = (1.0 / resultSize);
        double t = 0;
        for (int i = 0; i < resultSize; i++) {
            t += dt;
            KVector v = new KVector();
            for (int j = 0; j <= n; j++) {
                KVector p = controlPoints[j];
                double factor = binomiald(n, j) * powd(1 - t, n - j) * powd(t, j);
                v.x += p.x * factor;
                v.y += p.y * factor;
            }
            result[i] = v;
        }
        return result;
    }

    /**
     * Compute a number of approximation points on the Bezier curve defined by the given control
     * points. The degree of the curve is derived from the number of control points. The array of
     * resulting curve points includes the target point, but does not include the source point of
     * the curve. The number of approximation points is derived from the given control points.
     * 
     * @param controlPoints the control points
     * @return points on the curve defined by the given control points
     */
    public static KVector[] approximateBezierSegment(final KVector... controlPoints) {
        // The number of approximation points simply equals the number of control points.
        // Although there might be more accurate approximations, this approach is the fastest.
        int approximationCount = controlPoints.length + 1;
        return approximateBezierSegment(approximationCount, controlPoints);
    }

    /**
     * Compute an approximation for the spline that is defined by the given control points. The
     * control points are interpreted as a series of cubic Bezier curves.
     * 
     * @param controlPoints
     *            control points of a piecewise cubic spline
     * @return a vector chain that approximates the spline
     */
    public static KVectorChain approximateBezierSpline(final KVectorChain controlPoints) {
        int ctrlPtCount = controlPoints.size();
        KVectorChain spline = new KVectorChain();
        ListIterator<KVector> controlIter = controlPoints.listIterator();
        KVector currentPoint = controlIter.next();
        spline.add(currentPoint);
        while (controlIter.hasNext()) {
            int remainingPoints = ctrlPtCount - controlIter.nextIndex();
            if (remainingPoints == 1) {
                spline.add(controlIter.next());
            } else if (remainingPoints == 2) {
                // calculate a quadratic bezier curve
                spline.addAll(approximateBezierSegment(currentPoint, controlIter.next(),
                        controlIter.next()));
            } else {
                // calculate a cubic bezier curve
                KVector control1 = controlIter.next();
                KVector control2 = controlIter.next();
                KVector nextPoint = controlIter.next();
                spline.addAll(approximateBezierSegment(currentPoint, control1, control2, nextPoint));
                currentPoint = nextPoint;
            }
        }
        return spline;
    }

    /** degree of splines equation to find roots. */
    private static final int W_DEGREE = 5;

    /**
     * Calculate the distance from a cubic spline curve to the point {@code needle}.
     * 
     * @param start
     *            starting point
     * @param c1
     *            control point 1
     * @param c2
     *            control point 2
     * @param end
     *            end point
     * @param needle
     *            point to look for
     * @return distance from needle to curve
     */
    public static double distanceFromBezierSegment(final KVector start, final KVector c1,
            final KVector c2, final KVector end, final KVector needle) {
        double[] tCandidate = new double[W_DEGREE]; // possible roots
        KVector[] v = { start, c1, c2, end };

        // convert problem to 5th-degree Bezier form
        KVector[] w = convertToBezierForm(v, needle);

        // Find all possible roots of 5th-degree equation
        int nSolutions = findRoots(w, W_DEGREE, tCandidate, 0);

        // Compare distances of P5 to all candidates, and to t=0, and t=1
        // Check distance to beginning of curve, where t = 0
        double minDistance = needle.distance(start);
        double t = 0.0;

        // Find distances for candidate points
        for (int i = 0; i < nSolutions; i++) {
            KVector p = bezier(v, DEGREE, tCandidate[i], null, null);
            double distance = needle.distance(p);
            if (distance < minDistance) {
                minDistance = distance;
                t = tCandidate[i];
            }
        }

        // Finally, look at distance to end point, where t = 1.0
        double distance = needle.distance(end);
        if (distance < minDistance) {
            t = 1.0;
        }

        // Return the point on the curve at parameter value t
        KVector pn = new KVector(bezier(v, DEGREE, t, null, null));
        return Math.sqrt(pn.distance(needle));
    }

    /** cubic Bezier curves. */
    private static final int DEGREE = 3;
    /** precomputed "z" for cubics. */
    private static final double[][] CUBIC_Z = { { 1.0, 0.6, 0.3, 0.1 }, { 0.4, 0.6, 0.6, 0.4 },
            { 0.1, 0.3, 0.6, 1.0 }, };

    /**
     * Given a point and a Bezier curve, generate a 5th-degree Bezier-format equation whose solution
     * finds the point on the curve nearest the user-defined point.
     */
    private static KVector[] convertToBezierForm(final KVector[] v, final KVector pa) {
        KVector[] c = new KVector[DEGREE + 1]; // v(i) - pa
        KVector[] d = new KVector[DEGREE]; // v(i+1) - v(i)
        double[][] cdTable = new double[DEGREE][DEGREE + 1]; // dot product of c, d
        KVector[] w = new KVector[W_DEGREE + 1]; // ctl pts of 5th-degree curve

        // Determine the c's -- these are vectors created by subtracting
        // point pa from each of the control points
        for (int i = 0; i <= DEGREE; i++) {
            c[i] = new KVector(v[i].x - pa.x, v[i].y - pa.y);
        }

        // Determine the d's -- these are vectors created by subtracting
        // each control point from the next
        double s = DEGREE;
        for (int i = 0; i <= DEGREE - 1; i++) {
            d[i] = new KVector(s * (v[i + 1].x - v[i].x), s * (v[i + 1].y - v[i].y));
        }

        // Create the c,d table -- this is a table of dot products of the
        // c's and d's */
        for (int row = 0; row <= DEGREE - 1; row++) {
            for (int column = 0; column <= DEGREE; column++) {
                cdTable[row][column] = (d[row].x * c[column].x) + (d[row].y * c[column].y);
            }
        }

        // Now, apply the z's to the dot products, on the skew diagonal
        // Also, set up the x-values, making these "points"
        for (int i = 0; i <= W_DEGREE; i++) {
            w[i] = new KVector((double) (i) / W_DEGREE, 0.0);
        }

        int n = DEGREE;
        int m = DEGREE - 1;
        for (int k = 0; k <= n + m; k++) {
            int lb = Math.max(0, k - m);
            int ub = Math.min(k, n);
            for (int i = lb; i <= ub; i++) {
                int j = k - i;
                w[i + j].y = w[i + j].y + cdTable[j][i] * CUBIC_Z[j][i];
            }
        }

        return w;
    }

    /** maximum depth for recursion. */
    private static final int MAXDEPTH = 64;

    /**
     * Given a 5th-degree equation in Bernstein-Bezier form, find all of the roots in the interval
     * [0, 1].
     * 
     * @return the number of roots found.
     */
    private static int findRoots(final KVector[] w, final int degree, final double[] t,
            final int depth) {
        switch (crossingCount(w, degree)) {
        case 0: // No solutions here
            return 0;
        case 1: // Unique solution
            // Stop recursion when the tree is deep enough
            // if deep enough, return 1 solution at midpoint
            if (depth >= MAXDEPTH) {
                t[0] = (w[0].x + w[W_DEGREE].x) / 2.0;
                return 1;
            }
            if (controlPolygonFlatEnough(w, degree)) {
                t[0] = computeXIntercept(w, degree);
                return 1;
            }
            break;
        default: // nothing
        }

        // Otherwise, solve recursively after subdividing control polygon
        KVector[] left = new KVector[W_DEGREE + 1]; // New left and right
        KVector[] right = new KVector[W_DEGREE + 1]; // control polygons
        double[] leftT = new double[W_DEGREE + 1]; // Solutions from kids
        double[] rightT = new double[W_DEGREE + 1];

        // start in the middle of the bezier curve, t=0.5
        bezier(w, degree, 1.0 / 2, left, right);
        int leftCount = findRoots(left, degree, leftT, depth + 1);
        int rightCount = findRoots(right, degree, rightT, depth + 1);

        // Gather solutions together
        for (int i = 0; i < leftCount; i++) {
            t[i] = leftT[i];
        }
        for (int i = 0; i < rightCount; i++) {
            t[i + leftCount] = rightT[i];
        }

        // Send back total number of solutions */
        return leftCount + rightCount;
    }

    /** Flatness. */
    private static final double EPSILON = 1.0 * Math.pow(2, -MAXDEPTH - 1);

    /**
     * Check if the control polygon of a Bezier curve is flat enough for recursive subdivision to
     * bottom out.
     */
    private static boolean controlPolygonFlatEnough(final KVector[] v, final int degree) {

        // Find the perpendicular distance from each interior control point to
        // line connecting v[0] and v[degree]

        // Derive the implicit equation for line connecting first
        // and last control points
        double a = v[0].y - v[degree].y;
        double b = v[degree].x - v[0].x;
        double c = v[0].x * v[degree].y - v[degree].x * v[0].y;

        double abSquared = (a * a) + (b * b);
        double[] distance = new double[degree + 1]; // Distances from pts to line

        for (int i = 1; i < degree; i++) {
            // Compute distance from each of the points to that line
            distance[i] = a * v[i].x + b * v[i].y + c;
            if (distance[i] > 0.0) {
                distance[i] = (distance[i] * distance[i]) / abSquared;
            }
            if (distance[i] < 0.0) {
                distance[i] = -((distance[i] * distance[i]) / abSquared);
            }
        }

        // Find the largest distance
        double maxDistanceAbove = 0.0;
        double maxDistanceBelow = 0.0;
        for (int i = 1; i < degree; i++) {
            if (distance[i] < 0.0) {
                maxDistanceBelow = Math.min(maxDistanceBelow, distance[i]);
            }
            if (distance[i] > 0.0) {
                maxDistanceAbove = Math.max(maxDistanceAbove, distance[i]);
            }
        }

        // Implicit equation for zero line
        double a1 = 0.0;
        double b1 = 1.0;
        double c1 = 0.0;

        // Implicit equation for "above" line
        double a2 = a;
        double b2 = b;
        double c2 = c + maxDistanceAbove;

        double det = a1 * b2 - a2 * b1;
        double dInv = 1.0 / det;

        double intercept1 = (b1 * c2 - b2 * c1) * dInv;

        // Implicit equation for "below" line
        a2 = a;
        b2 = b;
        c2 = c + maxDistanceBelow;

        det = a1 * b2 - a2 * b1;
        dInv = 1.0 / det;

        double intercept2 = (b1 * c2 - b2 * c1) * dInv;

        // Compute intercepts of bounding box
        double leftIntercept = Math.min(intercept1, intercept2);
        double rightIntercept = Math.max(intercept1, intercept2);

        double error = (rightIntercept - leftIntercept) / 2;

        return error < EPSILON;
    }

    /**
     * Compute intersection of chord from first control point to last with 0-axis.
     */
    private static double computeXIntercept(final KVector[] v, final int degree) {
        double xnm = v[degree].x - v[0].x;
        double ynm = v[degree].y - v[0].y;
        double xmk = v[0].x;
        double ymk = v[0].y;

        double detInv = -1.0 / ynm;

        return (xnm * ymk - ynm * xmk) * detInv;
    }

    /**
     * Count the number of times a Bezier control polygon crosses the 0-axis. This number is >= the
     * number of roots.
     */
    private static int crossingCount(final KVector[] v, final int degree) {
        int nCrossings = 0;
        int sign = v[0].y < 0 ? -1 : 1;
        int oldSign = sign;
        for (int i = 1; i <= degree; i++) {
            sign = v[i].y < 0 ? -1 : 1;
            if (sign != oldSign) {
                nCrossings++;
            }
            oldSign = sign;
        }
        return nCrossings;
    }

    /**
     * Compute bezier curve.
     * 
     * @param c
     *            control points
     * @param degree
     *            degree of curve
     * @param t
     *            parameter for bezier function
     */
    private static KVector bezier(final KVector[] c, final int degree, final double t,
            final KVector[] left, final KVector[] right) {
        KVector[][] p = new KVector[W_DEGREE + 1][W_DEGREE + 1];

        for (int j = 0; j <= degree; j++) {
            p[0][j] = new KVector(c[j]);
        }

        for (int i = 1; i <= degree; i++) {
            for (int j = 0; j <= degree - i; j++) {
                p[i][j] = new KVector((1.0 - t) * p[i - 1][j].x + t * p[i - 1][j + 1].x, (1.0 - t)
                        * p[i - 1][j].y + t * p[i - 1][j + 1].y);
            }
        }

        if (left != null) {
            for (int j = 0; j <= degree; j++) {
                left[j] = p[j][0];
            }
        }

        if (right != null) {
            for (int j = 0; j <= degree; j++) {
                right[j] = p[degree - j][j];
            }
        }
        return p[degree][0];
    }

    /**
     * Determines the maximum for an arbitrary number of integers.
     * 
     * @param values
     *            integer values
     * @return the maximum of the given values, or {@code MIN_VALUE} if no values are given
     */
    public static int maxi(final int... values) {
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < values.length; i++) {
            if (values[i] > max) {
                max = values[i];
            }
        }
        return max;
    }

    /**
     * Determines the minimum for an arbitrary number of integers.
     * 
     * @param values
     *            integer values
     * @return the minimum of the given values, or {@code MAX_VALUE} if no values are given
     */
    public static int mini(final int... values) {
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < values.length; i++) {
            if (values[i] < min) {
                min = values[i];
            }
        }
        return min;
    }

    /**
     * Determines the average for an arbitrary number of integers.
     * 
     * @param values
     *            integer values
     * @return the average of the given values
     */
    public static int averagei(final int... values) {
        int avg = 0;
        for (int i = 0; i < values.length; i++) {
            avg += values[i];
        }
        return avg / values.length;
    }

    /**
     * Determines the maximum for an arbitrary number of long integers.
     * 
     * @param values
     *            integer values
     * @return the maximum of the given values, or {@code MIN_VALUE} if no values are given
     */
    public static long maxl(final long... values) {
        long max = Integer.MIN_VALUE;
        for (int i = 0; i < values.length; i++) {
            if (values[i] > max) {
                max = values[i];
            }
        }
        return max;
    }

    /**
     * Determines the minimum for an arbitrary number of long integers.
     * 
     * @param values
     *            integer values
     * @return the minimum of the given values, or {@code MAX_VALUE} if no values are given
     */
    public static long minl(final long... values) {
        long min = Integer.MAX_VALUE;
        for (int i = 0; i < values.length; i++) {
            if (values[i] < min) {
                min = values[i];
            }
        }
        return min;
    }

    /**
     * Determines the average for an arbitrary number of long integers.
     * 
     * @param values
     *            integer values
     * @return the average of the given values
     */
    public static long averagel(final long... values) {
        long avg = 0;
        for (int i = 0; i < values.length; i++) {
            avg += values[i];
        }
        return avg / values.length;
    }

    /**
     * Determines the maximum for an arbitrary number of floats.
     * 
     * @param values
     *            float values
     * @return the maximum of the given values, or {@code -MAX_VALUE} if no values are given
     */
    public static float maxf(final float... values) {
        float max = -Float.MAX_VALUE;
        for (int i = 0; i < values.length; i++) {
            if (values[i] > max) {
                max = values[i];
            }
        }
        return max;
    }

    /**
     * Determines the minimum for an arbitrary number of floats.
     * 
     * @param values
     *            float values
     * @return the minimum of the given values, or {@code MAX_VALUE} if no values are given
     */
    public static float minf(final float... values) {
        float min = Float.MAX_VALUE;
        for (int i = 0; i < values.length; i++) {
            if (values[i] < min) {
                min = values[i];
            }
        }
        return min;
    }

    /**
     * Determines the average for an arbitrary number of floats.
     * 
     * @param values
     *            float values
     * @return the average of the given values
     */
    public static float averagef(final float... values) {
        float avg = 0;
        for (int i = 0; i < values.length; i++) {
            avg += values[i];
        }
        return avg / values.length;
    }

    /**
     * Determines the maximum for an arbitrary number of doubles.
     * 
     * @param values
     *            double values
     * @return the maximum of the given values, or {@code -MAX_VALUE} if no values are given
     */
    public static double maxd(final double... values) {
        double max = -Double.MAX_VALUE;
        for (int i = 0; i < values.length; i++) {
            if (values[i] > max) {
                max = values[i];
            }
        }
        return max;
    }

    /**
     * Determines the minimum for an arbitrary number of doubles.
     * 
     * @param values
     *            double values
     * @return the minimum of the given values, or {@code MAX_VALUE} if no values are given
     */
    public static double mind(final double... values) {
        double min = Double.MAX_VALUE;
        for (int i = 0; i < values.length; i++) {
            if (values[i] < min) {
                min = values[i];
            }
        }
        return min;
    }

    /**
     * Determines the average for an arbitrary number of doubles.
     * 
     * @param values
     *            double values
     * @return the average of the given values
     */
    public static double averaged(final double... values) {
        double avg = 0;
        for (int i = 0; i < values.length; i++) {
            avg += values[i];
        }
        return avg / values.length;
    }
    
    /**
     * Limit the given integer to a specific range.
     * 
     * @param x an integer value
     * @param lower the lower limit
     * @param upper the upper limit
     * @return if x is beyond the limits, return the limit, otherwise just return x
     */
    public static int boundi(final int x, final int lower, final int upper) {
        if (x <= lower) {
            return lower;
        } else if (x >= upper) {
            return upper;
        }
        return x;
    }
    
    /**
     * Limit the given long to a specific range.
     * 
     * @param x an long value
     * @param lower the lower limit
     * @param upper the upper limit
     * @return if x is beyond the limits, return the limit, otherwise just return x
     */
    public static long boundl(final long x, final long lower, final long upper) {
        if (x <= lower) {
            return lower;
        } else if (x >= upper) {
            return upper;
        }
        return x;
    }
    
    /**
     * Limit the given float to a specific range.
     * 
     * @param x a float value
     * @param lower the lower limit
     * @param upper the upper limit
     * @return if x is beyond the limits, return the limit, otherwise just return x
     */
    public static float boundf(final float x, final float lower, final float upper) {
        if (x <= lower) {
            return lower;
        } else if (x >= upper) {
            return upper;
        }
        return x;
    }
    
    /**
     * Limit the given double to a specific range.
     * 
     * @param x a double value
     * @param lower the lower limit
     * @param upper the upper limit
     * @return if x is beyond the limits, return the limit, otherwise just return x
     */
    public static double boundd(final double x, final double lower, final double upper) {
        if (x <= lower) {
            return lower;
        } else if (x >= upper) {
            return upper;
        }
        return x;
    }
    
    /**
     * Clip the given vector to a rectangular box of given size.
     * 
     * @param v vector relative to the center of the box
     * @param width width of the rectangular box
     * @param height height of the rectangular box
     * 
     * @return {@code v}.
     */
    public static KVector clipVector(final KVector v, final double width, final double height) {
        double wh = width / 2, hh = height / 2;
        double absx = Math.abs(v.x), absy = Math.abs(v.y);
        double xscale = 1, yscale = 1;
        if (absx > wh) {
            xscale = wh / absx;
        }
        if (absy > hh) {
            yscale = hh / absy;
        }
        v.scale(Math.min(xscale, yscale));
        return v;
    }
    
    /**
     * Returns the signum function of the specified <tt>double</tt> value. The return value
     * is -1 if the specified value is negative; 0 if the specified value is zero; and 1 if the
     * specified value is positive. This is basically {@link Math#signum(double)} with an integer
     * return value.
     *
     * @return the signum function of the specified <tt>double</tt> value.
     */
    public static int signum(final double x) {
        if (x < 0) {
            return -1;
        }
        if (x > 0) {
            return 1;
        }
        return 0;
    }

}
