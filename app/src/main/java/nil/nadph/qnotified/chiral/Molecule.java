/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 cinit@github.com
 * https://github.com/cinit/QNotified
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
package nil.nadph.qnotified.chiral;

import nil.nadph.qnotified.util.IndexFrom;

public class Molecule implements Cloneable {

    private Atom[] atoms;
    private Bond[] bonds;
    private float maxX = 0.0f;
    private float maxY = 0.0f;
    private float minX = 0.0f;
    private float minY = 0.0f;
    private int numAtoms = 0;
    private int numBonds = 0;
    private boolean invalMinMax = true;


    private void determineMinMax() {
        this.invalMinMax = false;
        if (numAtoms == 0) {
            this.maxY = 0.0f;
            this.minY = 0.0f;
            this.maxX = 0.0f;
            this.minX = 0.0f;
            return;
        }
        float atomX = atomX(1);
        this.maxX = atomX;
        this.minX = atomX;
        float atomY = atomY(1);
        this.maxY = atomY;
        this.minY = atomY;
        for (int n = 2; n <= numAtoms; n++) {
            float x = atomX(n);
            float y = atomY(n);
            this.minX = Math.min(this.minX, x);
            this.maxX = Math.max(this.maxX, x);
            this.minY = Math.min(this.minY, y);
            this.maxY = Math.max(this.maxY, y);
        }
    }

    public Atom getAtom(@IndexFrom(1) int N) {
        if (N >= 1 && N <= this.numAtoms) {
            return this.atoms[N - 1];
        }
        throw new IndexOutOfBoundsException("Atoms: get " + N + ", numAtoms=" + this.numAtoms);
    }

    public Bond getBond(@IndexFrom(1) int N) {
        if (N >= 1 && N <= this.numBonds) {
            return this.bonds[N - 1];
        }
        throw new IndexOutOfBoundsException("Bonds: get " + N + ", numBonds=" + this.numBonds);
    }

    public int atomCount() {
        return numAtoms;
    }

    public int bondCount() {
        return numBonds;
    }

    public float atomX(@IndexFrom(1) int N) {
        return getAtom(N).x;
    }

    public float atomY(@IndexFrom(1) int N) {
        return getAtom(N).y;
    }


    public float atomZ(@IndexFrom(1) int N) {
        return getAtom(N).z;
    }

    public float rangeX() {
        return maxX() - minX();
    }

    public float rangeY() {
        return maxY() - minY();
    }

    public float minX() {
        if (this.invalMinMax) {
            determineMinMax();
        }
        return this.minX;
    }

    public float maxX() {
        if (this.invalMinMax) {
            determineMinMax();
        }
        return this.maxX;
    }

    public float minY() {
        if (this.invalMinMax) {
            determineMinMax();
        }
        return this.minY;
    }

    public float maxY() {
        if (this.invalMinMax) {
            determineMinMax();
        }
        return this.maxY;
    }


    public static final class Atom {
        public int charge;
        public String element;
        public String[] extra;
        public int hexplicit;
        public int isotope;
        public int mapnum;
        public String[] Transient;
        public int unpaired;
        public float x;
        public float y;
        public float z;
    }

    public static final class Bond {
        public String[] extra;
        public int from;
        public int order;
        public int to;
        public String[] Transient;
        public int type;
    }
}
