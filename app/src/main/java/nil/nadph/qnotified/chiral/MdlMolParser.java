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

public class MdlMolParser {

    public static class BadMolFormatException extends Exception {
        public BadMolFormatException(String msg) {
            super(msg);
        }
    }

    public static Molecule parseString(String str) throws BadMolFormatException {
        int start = -1;
        Molecule.Atom[] atoms;
        Molecule.Bond[] bonds;
        String[] lines = str.replace("\r\n", "\n").replace('\r', '\n').split("\n");
        for (int i = 0, linesLength = lines.length; i < linesLength; i++) {
            String line = lines[i];
            if ((line.length() >= 39 && line.startsWith("V2000", 34))) {
                start = i;
                break;
            }
        }
        if (start == -1)
            throw new BadMolFormatException("V2000 tag not found at any_line.substring(34, 39)");
        int numAtoms = Integer.parseInt(lines[start].substring(0, 3).trim());
        int numBonds = Integer.parseInt(lines[start].substring(3, 6).trim());
        atoms = new Molecule.Atom[numAtoms];
        bonds = new Molecule.Bond[numBonds];
        for (int i = 0; i < numAtoms; i++) {
            Molecule.Atom atom = new Molecule.Atom();
            atoms[i] = atom;
            String line = lines[start + 1 + i];
            if (line.length() < 39) {
                throw new BadMolFormatException("Invalid MDL MOL: atom line" + (start + 2 + i));
            }
            atom.x = Float.parseFloat(line.substring(0, 10).trim());
            atom.y = Float.parseFloat(line.substring(10, 20).trim());
            atom.z = Float.parseFloat(line.substring(20, 30).trim());
            atom.element = line.substring(31, 34).trim();
            int chg, rad = 0;
            int chg2 = Integer.parseInt(line.substring(36, 39).trim());
            int mapnum = line.length() < 63 ? 0 : Integer.parseInt(line.substring(60, 63).trim());
            if (chg2 >= 1 && chg2 <= 3) {
                chg = 4 - chg2;
            } else if (chg2 == 4) {
                chg = 0;
                rad = 2;
            } else if (chg2 < 5 || chg2 > 7) {
                chg = 0;
            } else {
                chg = 4 - chg2;
            }
            atom.charge = chg;
            atom.unpaired = rad;
            atom.mapnum = mapnum;
        }
        for (int i = 0; i < numBonds; i++) {
            Molecule.Bond bond = new Molecule.Bond();
            bonds[i] = bond;
            String line = lines[start + numAtoms + 1 + i];
            if (line.length() < 12) {
                throw new BadMolFormatException("Invalid MDL MOL: bond line" + (start + numAtoms + 2 + i));
            }
            int from = Integer.parseInt(line.substring(0, 3).trim());
            int to = Integer.parseInt(line.substring(3, 6).trim());
            int type = Integer.parseInt(line.substring(6, 9).trim());
            int stereo = Integer.parseInt(line.substring(9, 12).trim());
            if (from == to || from < 1 || from > numAtoms || to < 1 || to > numAtoms) {
                throw new BadMolFormatException("Invalid MDL MOL: bond line" + (start + numAtoms + 2 + i));
            }
            int order = (type < 1 || type > 3) ? 1 : type;
            int style = 0;
            if (stereo == 1) {
                style = 1;
            } else if (stereo == 6) {
                style = 2;
            }
            bond.from = from;
            bond.to = to;
            bond.type = order;
            bond.stereoDirection = style;
        }
        Molecule molecule = new Molecule(atoms, bonds, str);
        for (int i = start + numAtoms + numBonds + 1; i < lines.length; i++) {
            String line = lines[i];
            if (line.startsWith("M  END")) {
                break;
            }
            int type2 = 0;
            if (line.startsWith("M  CHG")) {
                type2 = 1;
            } else if (line.startsWith("M  RAD")) {
                type2 = 2;
            } else if (line.startsWith("M  ISO")) {
                type2 = 3;
            } else if (line.startsWith("M  RGP")) {
                type2 = 4;
            } else if (line.startsWith("M  HYD")) {
                type2 = 5;
            } else if (line.startsWith("M  ZCH")) {
                type2 = 6;
            } else if (!line.startsWith("M  ZBO")) {
                int anum = 0;
                try {
                    anum = Integer.parseInt(line.substring(3, 6).trim());
                } catch (NumberFormatException ignored) {
                }
                if (line.startsWith("A  ") && line.length() >= 6 && anum >= 1 && anum <= numAtoms) {
                    String line5 = lines[++i];
                    if (line5 == null) {
                        break;
                    }
                    molecule.getAtom(anum).element = line5;
                }
            } else {
                type2 = 7;
            }
            if (type2 > 0) {
                try {
                    int len = Integer.parseInt(line.substring(6, 9).trim());
                    for (int n3 = 0; n3 < len; n3++) {
                        int pos = Integer.parseInt(line.substring((n3 * 8) + 9, (n3 * 8) + 13).trim());
                        int val = Integer.parseInt(line.substring((n3 * 8) + 13, (n3 * 8) + 17).trim());
                        if (pos < 1) {
                            throw new BadMolFormatException("Invalid MDL MOL: M-block");
                        }
                        if (type2 == 1) {
                            molecule.getAtom(pos).charge = val;
                        } else if (type2 == 2) {
                            molecule.getAtom(pos).unpaired = val;
                        } else if (type2 == 3) {
                            molecule.getAtom(pos).isotope = val;
                        } else if (type2 == 4) {
                            molecule.getAtom(pos).element = "R" + val;
                        } else if (type2 == 5) {
                            molecule.getAtom(pos).showFlag = Molecule.SHOW_FLAG_EXPLICIT;
                        } else if (type2 == 6) {
                            molecule.getAtom(pos).charge = val;
                        } else if (type2 == 7) {
                            molecule.getBond(pos).stereoDirection = val;
                        }
                    }
                    continue;
                } catch (IndexOutOfBoundsException e) {
                    throw new BadMolFormatException("Invalid MDL MOL: M-block");
                }
            }
        }
        molecule.initOnce();
        return molecule;
    }
}
