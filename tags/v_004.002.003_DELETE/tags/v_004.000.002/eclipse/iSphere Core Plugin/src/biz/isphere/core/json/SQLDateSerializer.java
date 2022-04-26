/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.json;

import java.lang.reflect.Type;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class SQLDateSerializer implements JsonSerializer<Date>, JsonDeserializer<Date> {

    public JsonElement serialize(Date src, Type srcType, JsonSerializationContext context) {
        return src == null ? null : new JsonPrimitive(new SimpleDateFormat("yyyy-MM-dd").format(src));
    }

    public Date deserialize(JsonElement json, Type jsonType, JsonDeserializationContext context) throws JsonParseException {
        try {
            return json == null ? null : new Date(new SimpleDateFormat("yyyy-MM-dd").parse(json.getAsString()).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

}
