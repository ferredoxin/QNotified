package nil.nadph.qnotified.chiral;

import nil.nadph.qnotified.util.IndexFrom;

import java.util.ArrayList;
import java.util.HashSet;

public class ChiralCarbonHelper {

    public static HashSet<Integer> getMoleculeChiralCarbons(Molecule mol) {
        HashSet<Integer> ret = new HashSet<>();
        for (int i = 1; i <= mol.atomCount(); i++) {
            if (isChiralCarbon(mol, i)) {
                ret.add(i);
            }
        }
        return ret;
    }

    public static boolean isChiralCarbon(Molecule mol, @IndexFrom(1) int index) {
        Molecule.Atom atom = mol.getAtom(index);
        if (!"C".equals(atom.element)) return false;
        Molecule.Bond[] bonds = mol.getAtomDeclaredBonds(index);
        for (Molecule.Bond b : bonds) {
            if (b.type > 1) return false;
        }
        int hcnt = atom.hydrogenCount;
        ArrayList<Molecule.Bond> bondnh = new ArrayList<>(4);
        for (int i = 0; i < bonds.length; i++) {
            Molecule.Bond b = bonds[i];
            int another = (b.from == index) ? b.to : b.from;
            if ("H".equals(mol.getAtom(another).element) && mol.getAtomDeclaredBonds(another).length == 1) {
                hcnt++;
                bonds[i] = null;
            } else {
                bondnh.add(b);
            }
        }
        if (bondnh.size() == 4 && hcnt == 0) {
            int b1 = mol.getBondId(bondnh.get(0));
            int b2 = mol.getBondId(bondnh.get(1));
            int b3 = mol.getBondId(bondnh.get(2));
            int b4 = mol.getBondId(bondnh.get(3));
            return !(compareChain(mol, index, b1, b2) || compareChain(mol, index, b1, b3) || compareChain(mol, index, b1, b4)
                    || compareChain(mol, index, b2, b3) || compareChain(mol, index, b2, b4) || compareChain(mol, index, b3, b4));
        } else if (bondnh.size() == 3 && hcnt == 1) {
            int b1 = mol.getBondId(bondnh.get(0));
            int b2 = mol.getBondId(bondnh.get(1));
            int b3 = mol.getBondId(bondnh.get(2));
            return !(compareChain(mol, index, b1, b2) || compareChain(mol, index, b1, b3) || compareChain(mol, index, b3, b2));
        } else {
            return false;
        }
    }

    @IndexFrom(1)
    public static boolean compareChain(Molecule mol, int center, int chain1, int chain2) {
        return compareChain(mol, center, center, chain1, chain2, (int) (3 + Math.sqrt(mol.atomCount())));
    }

    @IndexFrom(1)
    public static boolean compareChain(Molecule mol, int atom1, int atom2, int chain1, int chain2, int ttl) {
        Molecule.Bond b1 = mol.getBond(chain1);
        Molecule.Bond b2 = mol.getBond(chain2);
        if (b1.type != b2.type) return false;
        int another1 = (b1.from == atom1) ? b1.to : b1.from;
        int another2 = (b2.from == atom2) ? b2.to : b2.from;
        Molecule.Atom a1 = mol.getAtom(another1);
        Molecule.Atom a2 = mol.getAtom(another2);
        if (!a1.element.equals(a2.element)) return false;
        int hcnt1 = a1.hydrogenCount;
        int hcnt2 = a2.hydrogenCount;
        Molecule.Bond[] bonds1 = mol.getAtomDeclaredBonds(another1);
        Molecule.Bond[] bonds2 = mol.getAtomDeclaredBonds(another2);
        ArrayList<Molecule.Bond> bondnh1 = new ArrayList<>(4);
        ArrayList<Molecule.Bond> bondnh2 = new ArrayList<>(4);
        for (Molecule.Bond b : bonds1) {
            int another = (b.from == another1) ? b.to : b.from;
            if (another == atom1) continue;
            if ("H".equals(mol.getAtom(another).element) && mol.getAtomDeclaredBonds(another).length == 1) {
                hcnt1++;
            } else {
                bondnh1.add(b);
            }
        }
        for (Molecule.Bond b : bonds2) {
            int another = (b.from == another2) ? b.to : b.from;
            if (another == atom2) continue;
            if ("H".equals(mol.getAtom(another).element) && mol.getAtomDeclaredBonds(another).length == 1) {
                hcnt2++;
            } else {
                bondnh2.add(b);
            }
        }
        if (hcnt1 != hcnt2 || bondnh1.size() != bondnh2.size()) return false;
        if (ttl < 0) {
            //Utils.log(new IllegalArgumentException("ttl must > 0"));
            return true;
        }
        --ttl;
        for (Molecule.Bond dchain1 : bondnh1) {
            boolean success = false;
            for (Molecule.Bond bond : bondnh2) {
                if (compareChain(mol, another1, another2, mol.getBondId(dchain1), mol.getBondId(bond), ttl)) {
                    success = true;
                    break;
                }
            }
            if (!success) return false;
        }
        return true;
    }
}
