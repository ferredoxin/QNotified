/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 xenonhydride@gmail.com
 * https://github.com/ferredoxin/QNotified
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
package nil.nadph.qnotified.chiral;

import java.util.HashSet;

import nil.nadph.qnotified.util.IndexFrom;

public class Molecule implements Cloneable {

    private final Atom[] atoms;
    private final Bond[] bonds;
    private float maxX = 0.0f;
    private float maxY = 0.0f;
    private float minX = 0.0f;
    private float minY = 0.0f;
    private boolean invalMinMax = true;
    private float avgBondLength;
    private final String mdlMolStr;

    public Molecule(Atom[] a, Bond[] b, String mdlMol) {
        atoms = a;
        bonds = b;
        mdlMolStr = mdlMol;
    }

    private void determineMinMax() {
        this.invalMinMax = false;
        if (atoms.length == 0) {
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
        for (int n = 2; n <= atoms.length; n++) {
            float x = atomX(n);
            float y = atomY(n);
            this.minX = Math.min(this.minX, x);
            this.maxX = Math.max(this.maxX, x);
            this.minY = Math.min(this.minY, y);
            this.maxY = Math.max(this.maxY, y);
        }
    }

    public Atom getAtom(@IndexFrom(1) int N) {
        if (N >= 1 && N <= this.atoms.length) {
            return this.atoms[N - 1];
        }
        throw new IndexOutOfBoundsException("Atoms: get " + N + ", numAtoms=" + this.atoms.length);
    }

    public Bond getBond(@IndexFrom(1) int N) {
        if (N >= 1 && N <= this.bonds.length) {
            return this.bonds[N - 1];
        }
        throw new IndexOutOfBoundsException("Bonds: get " + N + ", numBonds=" + this.bonds.length);
    }

    @IndexFrom(1)
    public int getAtomIndexNear(float x, float y, float tolerance) {
        if (atoms.length == 0) return -1;
        int N = 1;
        float t1, t2, t3;
        t1 = atoms[0].x - x;
        t2 = atoms[0].y - y;
        float curr = t1 * t1 + t2 * t2;
        for (int i = 1; i < atoms.length; i++) {
            t1 = atoms[i].x - x;
            t2 = atoms[i].y - y;
            t3 = t1 * t1 + t2 * t2;
            if (t3 < curr) {
                N = i + 1;
                curr = t3;
            }
        }
        if (curr < tolerance * tolerance) return N;
        else return -1;
    }

    public int atomCount() {
        return atoms.length;
    }

    public int bondCount() {
        return bonds.length;
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

    public Bond[] getAtomDeclaredBonds(@IndexFrom(1) int N) {
        if (N >= 1 && N <= this.bonds.length) {
            HashSet<Bond> ret = new HashSet<>();
            for (Bond b : bonds) {
                if (b.from == N || b.to == N) {
                    ret.add(b);
                }
            }
            return ret.toArray(new Bond[0]);
        }
        throw new IndexOutOfBoundsException("getAtomBonds: get " + N + ", bondCount=" + this.bonds.length);
    }

    @IndexFrom(1)
    public int getAtomId(Atom atom) {
        for (int i = 0; i < atoms.length; i++) {
            if (atom == atoms[i]) {
                return i + 1;
            }
        }
        return -1;
    }

    @IndexFrom(1)
    public int getBondId(Bond atom) {
        for (int i = 0; i < bonds.length; i++) {
            if (atom == bonds[i]) {
                return i + 1;
            }
        }
        return -1;
    }

    public float getAverageBondLength() {
        return avgBondLength;
    }

    public void initOnce() {
        for (int i = 0, atomsLength = atoms.length; i < atomsLength; i++) {
            Atom atom = atoms[i];
            Bond[] bs = getAtomDeclaredBonds(i + 1);
            int ii = 0;
            for (Bond b : bs) {
                ii += b.type;
            }
            if (atom.hydrogenCount == 0) {
                switch (atom.element) {
                    case "C":
                        atom.hydrogenCount = Math.max(0, 4 - atom.unpaired - Math.abs(atom.charge) - ii);
                        break;
                    case "O":
                    case "S":
                        atom.hydrogenCount = Math.max(0, 2 - atom.unpaired + atom.charge - ii);
                        break;
                    case "N":
                    case "P":
                        atom.hydrogenCount = Math.max(0, 3 - atom.unpaired + atom.charge - ii);
                        break;
                    case "F":
                    case "Cl":
                    case "Br":
                    case "I":
                        atom.hydrogenCount = Math.max(0, 1 - atom.unpaired - Math.abs(atom.charge) - ii);
                        break;
                }
            }
            if (atom.element.equals("C")) {
                if (bs.length == 2) {
                    float t1 = (float) Math.atan2(atomY(bs[0].from) - atomY(bs[0].to), atomX(bs[0].from) - atomX(bs[0].to));
                    float t2 = (float) Math.atan2(atomY(bs[1].from) - atomY(bs[1].to), atomX(bs[1].from) - atomX(bs[1].to));
                    if (t1 < 0) t1 += Math.PI;
                    if (t2 < 0) t2 += Math.PI;
                    if (Math.abs(t1 - t2) < 10f / 360f * Math.PI * 2f) {
                        atom.showFlag |= SHOW_FLAG_EXPLICIT;
                    }
                }
            }
            float top, bottom, left, right;
            top = bottom = left = right = 6.28f;
            for (Bond b : bs) {
                float x1 = atomX(i + 1);
                float y1 = atomY(i + 1);
                float x2, y2;
                if (b.from == i + 1) {
                    x2 = atomX(b.to);
                    y2 = atomY(b.to);
                } else {
                    x2 = atomX(b.from);
                    y2 = atomY(b.from);
                }
                float dt = (float) Math.atan2(y2 - y1, x2 - x1);
                float tmp;
                tmp = Math.abs(dt - 0);
                if (tmp > Math.PI * 2) tmp -= Math.PI * 2;
                right = Math.min(right, tmp);
                tmp = (float) Math.min(Math.abs(dt - Math.PI), Math.abs(dt + Math.PI));
                if (tmp > Math.PI * 2) tmp -= Math.PI * 2;
                left = Math.min(left, tmp);
                tmp = (float) Math.abs(dt - Math.PI / 2f);
                if (tmp > Math.PI * 2) tmp -= Math.PI * 2;
                top = Math.min(top, tmp);
                tmp = (float) Math.abs(dt + Math.PI / 2);
                if (tmp > Math.PI * 2) tmp -= Math.PI * 2;
                bottom = Math.min(bottom, tmp);
            }
            if (right > 1.0f) {
                atom.spareSpace = DIRECTION_RIGHT;
            } else if (left > 1.4f) {
                atom.spareSpace = DIRECTION_LEFT;
            } else {
                float max = Math.max(Math.max(top, bottom), Math.max(left, right));
                if (max == right) {
                    atom.spareSpace = DIRECTION_RIGHT;
                } else if (max == left) {
                    atom.spareSpace = DIRECTION_LEFT;
                } else if (max == bottom) {
                    atom.spareSpace = DIRECTION_BOTTOM;
                } else if (max == top) {
                    atom.spareSpace = DIRECTION_TOP;
                }
            }
        }
        float sum = 0;
        Atom a1, a2;
        for (Bond b : bonds) {
            a1 = atoms[b.from - 1];
            a2 = atoms[b.to - 1];
            sum += Math.hypot(a1.x - a2.x, a1.y - a2.y);
        }
        avgBondLength = sum / bonds.length;
    }

    public String toMdlMolString() {
        return mdlMolStr;
    }

    public static final int SHOW_FLAG_DEFAULT = 0;
    public static final int SHOW_FLAG_EXPLICIT = 1;
    public static final int SHOW_FLAG_IMPLICIT = 2;

    public static final int DIRECTION_UNSPECIFIED = 0;
    public static final int DIRECTION_TOP = 1;
    public static final int DIRECTION_BOTTOM = 2;
    public static final int DIRECTION_LEFT = 4;
    public static final int DIRECTION_RIGHT = 8;

    public static final class Atom {
        public int charge;
        public String element;
        public int showFlag;
        public int hydrogenCount;
        public int spareSpace;
        public int isotope;
        public int mapnum;
        public int unpaired;
        public float x;
        public float y;
        public float z;
        public String[] extra;
    }

    public static final class Bond {
        public int from;
        public int to;
        public int type;
        public int stereoDirection;
        public String[] extra;
    }
}
