/**
 * Copyright (c) 2014 Jilk Systems, Inc.
 *
 * This file is part of the Java ROSBridge Client.
 *
 * The Java ROSBridge Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Java ROSBridge Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Java ROSBridge Client.  If not, see http://www.gnu.org/licenses/.
 *
 */
package com.example.myapplication.message;

@MessageType(string = "remaining_time/Remaining_time")
public class Time extends Message {
    public int hour;
    public int minute;
    public int second;

    public Time(){
        this.hour = 0;
        this.minute = 0;
        this.second = 0;
    }
}
