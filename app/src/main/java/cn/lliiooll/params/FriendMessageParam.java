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
package cn.lliiooll.params;

public class FriendMessageParam implements BaseParams {
    
    private String self;
    private String sender;
    private String msg;
    
    public FriendMessageParam(String self, String sender, String msg) {
        this.self = self;
        this.sender = sender;
        this.msg = msg;
    }
    
    public String getSelf() {
        return self;
    }
    
    public String getSender() {
        return sender;
    }
    
    public String getMsg() {
        return msg;
    }
    
    public static FriendMessageParamBuilder builder() {
        return new FriendMessageParamBuilder();
    }
    
    public static class FriendMessageParamBuilder {
        
        private String self;
        private String sender;
        private String msg;
        
        public FriendMessageParamBuilder setSelfUin(String self) {
            this.self = self;
            return this;
        }
        
        public FriendMessageParamBuilder setSenderUin(String sender) {
            this.sender = sender;
            return this;
        }
        
        public FriendMessageParamBuilder setMsg(String msg) {
            this.msg = msg;
            return this;
        }
        
        public FriendMessageParam build() {
            return new FriendMessageParam(self, sender, msg);
        }
    }
    
    @Override
    public String toString() {
        return "self:" + getSelf() + " sender:" + getSender() + " msg:" + getMsg();
    }
}
