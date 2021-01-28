/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 xenonhydride@gmail.com
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

import nil.nadph.qnotified.util.NonUiThread;
import nil.nadph.qnotified.util.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class PubChemStealer {

    private static final String PUB_CHEM_SITE = "http://127.0.0.1:8081";
    private static final String FAKE_PUB_CHEM_SITE = "https://ioctl.cc";//reserved proxy...

    @NonUiThread
    @Nullable
    public static Molecule nextRandomMolecule() {
        Random r = new Random();
        for (int retry = 5; retry > 0; retry--) {
            long cid = (long) (r.nextDouble() * 100000000 + r.nextDouble() * 10000000 + r.nextDouble() * 100000 + 100000);
            try {
                return getMoleculeByCid(cid);
            } catch (IOException e) {
                retry--;
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    @NonUiThread
    public static Molecule getMoleculeByCid(long cid) throws IOException, MdlMolParser.BadMolFormatException {
        HttpURLConnection conn = (HttpURLConnection) new URL(PUB_CHEM_SITE + "/rest/pug/compound/CID/" + cid + "/record/SDF/?record_type=2d&response_type=display").openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        if (conn.getResponseCode() != 200) {
            conn.disconnect();
            throw new IOException("Bad ResponseCode: " + conn.getResponseCode());
        }
        InputStream in = conn.getInputStream();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        in.close();
        conn.disconnect();
        String str = outStream.toString();
        return MdlMolParser.parseString(str);
    }
}
