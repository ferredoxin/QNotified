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
package cc.ioctl.script.params;

public class GroupRequestParam {
    /**
     * 请求id
     */
    public long senderuin;
    /**
     * 群id
     */
    public long uin;
    /**
     * 验证消息
     */
    public String content;

    public GroupRequestParam setUin(long uin) {
        this.uin = uin;
        return this;
    }

    public GroupRequestParam setSenderUin(long uin) {
        this.senderuin = uin;
        return this;
    }

    public GroupRequestParam setContent(String content) {
        this.content = content;
        return this;
    }

    public GroupRequestParam create() {
        return this;
    }

    /**
     * 接受请求
     */
    public void accept() {
        // to do
    }

    /**
     * 拒绝请求
     */
    public void refuse() {
        // to do
    }
}
