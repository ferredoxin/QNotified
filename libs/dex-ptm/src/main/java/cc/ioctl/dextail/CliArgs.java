/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2022 dmca@ioctl.cc
 * https://github.com/ferredoxin/QNotified
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by ferredoxin.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/ferredoxin/QNotified/blob/master/LICENSE.md>.
 */

package cc.ioctl.dextail;

import java.io.PrintStream;
import java.util.ArrayList;

public class CliArgs {

    public boolean showHelp = false;
    public boolean checkDex = false;
    public boolean showInfo = false;
    public boolean disableXor = false;
    public boolean extractPayload = false;
    public boolean useTimeAsPayload = false;

    public String dexFile = null;
    public String outputFile = null;
    public String payloadFile = null;

    public ArrayList<String> unrecognized = new ArrayList<>();

    public void parse(String[] args) throws IllegalArgumentException {
        boolean eoo = false;
        for (int i = 0; i < args.length; i++) {
            String s = args[i];
            if (s.startsWith("-")) {
                if (s.equals("-")) {
                    eoo = true;
                    continue;
                }
                switch (s) {
                    case "-h":
                    case "--help":
                        showHelp = true;
                        break;
                    case "-i":
                    case "--info":
                        showInfo = true;
                        break;
                    case "-e":
                    case "--extract":
                        extractPayload = true;
                        break;
                    case "--check":
                        checkDex = true;
                        break;
                    case "-x":
                        disableXor = true;
                        break;
                    case "--time":
                        useTimeAsPayload = true;
                        break;
                    case "-p":
                        if (i == args.length - 1) {
                            throw new IllegalArgumentException("-p requires a value");
                        }
                        payloadFile = args[++i];
                        break;
                    case "-o":
                        if (i == args.length - 1) {
                            throw new IllegalArgumentException("-o requires a value");
                        }
                        outputFile = args[++i];
                        break;
                    default:
                        unrecognized.add(s);
                }
            } else {
                eoo = true;
                if (dexFile == null) {
                    dexFile = s;
                } else {
                    throw new IllegalArgumentException("too many arguments");
                }
            }
        }
    }

    public void printHelp(PrintStream out) {
        if (out == null) {
            return;
        }
        out.println("Usage: [options] <dex file>");
        out.println("options: -h, --help    Show this help");
        out.println("         -x            Disable xor");
        out.println("         -i, --info    Show dex payload info");
        out.println("         -e, --extract Extract dex payload");
        out.println("             --check   Check whether dex checksum is correct");
        out.println("             --time    Use 8-byte(LE) timestamp in milliseconds as payload");
        out.println("         -p <payload>  Payload to add to dex");
        out.println("         -o <out>      Place the output dex into <out>");
    }

}
