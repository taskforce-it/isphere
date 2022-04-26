/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.json;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class SQLTimestampSerializer implements JsonSerializer<Timestamp>, JsonDeserializer<Timestamp> {

    public JsonElement serialize(Timestamp src, Type srcType, JsonSerializationContext context) {
        return src == null ? null : new JsonPrimitive(new SimpleDateFormat("yyy-MM-dd-hh.mm.ss.S").format(src));
    }

    public Timestamp deserialize(JsonElement json, Type srcType, JsonDeserializationContext arg2) throws JsonParseException {
        try {
            return json == null ? null : new Timestamp(new SimpleDateFormat("yyy-MM-dd-hh.mm.ss.S").parse(json.getAsString()).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

}
