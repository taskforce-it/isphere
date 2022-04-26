package org.tn5250j.settings;

/**
 * Title: ColorAttributesPanel
 * Copyright:   Copyright (c) 2001
 * Company:
 * @author  Kenneth J. Pouncey
 * @version 0.5
 *
 * Description:
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software; see the file COPYING.  If not, write to
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA
 *
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.tn5250j.SessionConfig;
import org.tn5250j.tools.LangTool;

public class ColorAttributesPanel extends AttributesPanel {

    private static final long serialVersionUID = 1L;
    JComboBox colorSchemaList;
    JComboBox colorList;
    JColorChooser jcc;
    Schema colorSchema;
    Properties schemaProps;

    public ColorAttributesPanel(SessionConfig config) {
        super(config, "Colors");
    }

    /** Component initialization */
    public void initPanel() throws Exception {

        JPanel cp = new JPanel();
        cp.setLayout(new BorderLayout());

        JPanel cschp = new JPanel();
        TitledBorder tb = BorderFactory.createTitledBorder(LangTool.getString("sa.colorSchema"));
        cschp.setBorder(tb);
        colorSchemaList = new JComboBox();
        loadSchemas(colorSchemaList);

        colorSchemaList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox)e.getSource();
                Object obj = cb.getSelectedItem();
                if (obj instanceof Schema) {
                    colorSchema = (Schema)obj;
                } else {
                    colorSchema = null;
                }

                // Update color of preview section
                colorList.setSelectedItem(colorList.getSelectedItem());
            }
        });

        cschp.add(colorSchemaList);

        tb = BorderFactory.createTitledBorder(LangTool.getString("sa.colors"));
        cp.setBorder(tb);
        colorList = new JComboBox();
        colorList.addItem(LangTool.getString("sa.bg"));
        colorList.addItem(LangTool.getString("sa.blue"));
        colorList.addItem(LangTool.getString("sa.red"));
        colorList.addItem(LangTool.getString("sa.pink"));
        colorList.addItem(LangTool.getString("sa.green"));
        colorList.addItem(LangTool.getString("sa.turq"));
        colorList.addItem(LangTool.getString("sa.yellow"));
        colorList.addItem(LangTool.getString("sa.white"));
        colorList.addItem(LangTool.getString("sa.guiField"));
        colorList.addItem(LangTool.getString("sa.cursorColor"));
        colorList.addItem(LangTool.getString("sa.columnSep"));
        colorList.addItem(LangTool.getString("sa.hexAttrColor"));

        jcc = new JColorChooser();

        // set the default color for display as that being for back ground
        jcc.setColor(getColorProperty(ColorProperty.BACKGROUND.key()));

        colorList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                JComboBox cb = (JComboBox)e.getSource();
                String newSelection = (String)cb.getSelectedItem();

                if (newSelection.equals(LangTool.getString("sa.bg"))) {
                    if (colorSchema != null)
                        jcc.setColor(colorSchema.getColorBg());
                    else
                        jcc.setColor(getColorProperty(ColorProperty.BACKGROUND.key()));
                }

                if (newSelection.equals(LangTool.getString("sa.blue"))) {
                    if (colorSchema != null)
                        jcc.setColor(colorSchema.getColorBlue());
                    else
                        jcc.setColor(getColorProperty(ColorProperty.BLUE.key()));
                }

                if (newSelection.equals(LangTool.getString("sa.red"))) {
                    if (colorSchema != null)
                        jcc.setColor(colorSchema.getColorRed());
                    else
                        jcc.setColor(getColorProperty(ColorProperty.RED.key()));

                }

                if (newSelection.equals(LangTool.getString("sa.pink"))) {
                    if (colorSchema != null)
                        jcc.setColor(colorSchema.getColorPink());
                    else
                        jcc.setColor(getColorProperty(ColorProperty.PINK.key()));

                }

                if (newSelection.equals(LangTool.getString("sa.green"))) {
                    if (colorSchema != null)
                        jcc.setColor(colorSchema.getColorGreen());
                    else
                        jcc.setColor(getColorProperty(ColorProperty.GREEN.key()));

                }

                if (newSelection.equals(LangTool.getString("sa.turq"))) {
                    if (colorSchema != null)
                        jcc.setColor(colorSchema.getColorTurq());
                    else
                        jcc.setColor(getColorProperty(ColorProperty.TURQUOISE.key()));

                }

                if (newSelection.equals(LangTool.getString("sa.yellow"))) {
                    if (colorSchema != null)
                        jcc.setColor(colorSchema.getColorYellow());
                    else
                        jcc.setColor(getColorProperty(ColorProperty.YELLOW.key()));

                }

                if (newSelection.equals(LangTool.getString("sa.white"))) {
                    if (colorSchema != null)
                        jcc.setColor(colorSchema.getColorWhite());
                    else
                        jcc.setColor(getColorProperty(ColorProperty.WHITE.key()));
                }

                if (newSelection.equals(LangTool.getString("sa.guiField"))) {
                    if (colorSchema != null)
                        jcc.setColor(colorSchema.getColorGuiField());
                    else
                        jcc.setColor(getColorProperty(ColorProperty.GUI_FIELD.key(), Color.white));
                }

                if (newSelection.equals(LangTool.getString("sa.cursorColor"))) {
                    if (colorSchema != null)
                        jcc.setColor(colorSchema.getColorBg());
                    else
                        jcc.setColor(getColorProperty(ColorProperty.CURSOR.key(), getColorProperty(ColorProperty.BACKGROUND.key())));
                }

                if (newSelection.equals(LangTool.getString("sa.columnSep"))) {
                    if (colorSchema != null)
                        jcc.setColor(colorSchema.getColorSeparator());
                    else
                        jcc.setColor(getColorProperty(ColorProperty.SEPARATOR.key(), getColorProperty(ColorProperty.WHITE.key())));
                }

                if (newSelection.equals(LangTool.getString("sa.hexAttrColor"))) {
                    if (colorSchema != null)
                        jcc.setColor(colorSchema.getColorHexAttr());
                    else
                        jcc.setColor(getColorProperty(ColorProperty.HEX_ATTR.key(), getColorProperty(ColorProperty.WHITE.key())));
                }
            }
        });

        cp.add(colorList, BorderLayout.NORTH);
        cp.add(jcc, BorderLayout.CENTER);

        add(cschp, BorderLayout.NORTH);
        add(cp, BorderLayout.CENTER);

    }

    public void save() {

    }

    public void applyAttributes() {

        String newSelection = (String)colorList.getSelectedItem();

        if (colorSchema != null) {

            /*
             * Reset all color attributes to the values provided by the schema.
             */

            if (!getColorProperty(ColorProperty.BACKGROUND.key()).equals(colorSchema.getColorBg())) {
                changes.firePropertyChange(this, ColorProperty.BACKGROUND.key(), getColorProperty(ColorProperty.BACKGROUND.key()),
                    colorSchema.getColorBg());
                setProperty(ColorProperty.BACKGROUND.key(), Integer.toString(colorSchema.getColorBg().getRGB()));
            }

            if (!getColorProperty(ColorProperty.BLUE.key()).equals(colorSchema.getColorBlue())) {
                changes.firePropertyChange(this, ColorProperty.BLUE.key(), getColorProperty(ColorProperty.BLUE.key()), colorSchema.getColorBlue());
                setProperty(ColorProperty.BLUE.key(), Integer.toString(colorSchema.getColorBlue().getRGB()));
            }

            if (!getColorProperty(ColorProperty.RED.key()).equals(colorSchema.getColorRed())) {
                changes.firePropertyChange(this, ColorProperty.RED.key(), getColorProperty(ColorProperty.RED.key()), colorSchema.getColorRed());
                setProperty(ColorProperty.RED.key(), Integer.toString(colorSchema.getColorRed().getRGB()));
            }

            if (!getColorProperty(ColorProperty.PINK.key()).equals(colorSchema.getColorPink())) {
                changes.firePropertyChange(this, ColorProperty.PINK.key(), getColorProperty(ColorProperty.PINK.key()), colorSchema.getColorPink());
                setProperty(ColorProperty.PINK.key(), Integer.toString(colorSchema.getColorPink().getRGB()));
            }

            if (!getColorProperty(ColorProperty.GREEN.key()).equals(colorSchema.getColorGreen())) {
                changes.firePropertyChange(this, ColorProperty.GREEN.key(), getColorProperty(ColorProperty.GREEN.key()), colorSchema.getColorGreen());
                setProperty(ColorProperty.GREEN.key(), Integer.toString(colorSchema.getColorGreen().getRGB()));
            }

            if (!getColorProperty(ColorProperty.TURQUOISE.key()).equals(colorSchema.getColorTurq())) {
                changes.firePropertyChange(this, ColorProperty.TURQUOISE.key(), getColorProperty(ColorProperty.TURQUOISE.key()),
                    colorSchema.getColorTurq());
                setProperty(ColorProperty.TURQUOISE.key(), Integer.toString(colorSchema.getColorTurq().getRGB()));
            }

            if (!getColorProperty(ColorProperty.YELLOW.key()).equals(colorSchema.getColorYellow())) {
                changes.firePropertyChange(this, ColorProperty.YELLOW.key(), getColorProperty(ColorProperty.YELLOW.key()),
                    colorSchema.getColorYellow());
                setProperty(ColorProperty.YELLOW.key(), Integer.toString(colorSchema.getColorYellow().getRGB()));
            }

            if (!getColorProperty(ColorProperty.WHITE.key()).equals(colorSchema.getColorWhite())) {
                changes.firePropertyChange(this, ColorProperty.WHITE.key(), getColorProperty(ColorProperty.WHITE.key()), colorSchema.getColorWhite());
                setProperty(ColorProperty.WHITE.key(), Integer.toString(colorSchema.getColorWhite().getRGB()));
            }

            if (!getColorProperty(ColorProperty.GUI_FIELD.key()).equals(colorSchema.getColorGuiField())) {
                changes.firePropertyChange(this, ColorProperty.GUI_FIELD.key(), getColorProperty(ColorProperty.GUI_FIELD.key()),
                    colorSchema.getColorGuiField());
                setProperty(ColorProperty.GUI_FIELD.key(), Integer.toString(colorSchema.getColorGuiField().getRGB()));
            }

            if (!getColorProperty(ColorProperty.CURSOR.key()).equals(colorSchema.getColorCursor())) {
                changes.firePropertyChange(this, ColorProperty.CURSOR.key(), getColorProperty(ColorProperty.CURSOR.key()),
                    colorSchema.getColorCursor());
                setProperty(ColorProperty.CURSOR.key(), Integer.toString(colorSchema.getColorCursor().getRGB()));
            }

            if (!getColorProperty(ColorProperty.SEPARATOR.key()).equals(colorSchema.getColorSeparator())) {
                changes.firePropertyChange(this, ColorProperty.SEPARATOR.key(), getColorProperty(ColorProperty.SEPARATOR.key()),
                    colorSchema.getColorSeparator());
                setProperty(ColorProperty.SEPARATOR.key(), Integer.toString(colorSchema.getColorSeparator().getRGB()));
            }

            if (!getColorProperty(ColorProperty.HEX_ATTR.key()).equals(colorSchema.getColorHexAttr())) {
                changes.firePropertyChange(this, ColorProperty.HEX_ATTR.key(), getColorProperty(ColorProperty.HEX_ATTR.key()),
                    colorSchema.getColorHexAttr());
                setProperty(ColorProperty.HEX_ATTR.key(), Integer.toString(colorSchema.getColorHexAttr().getRGB()));
            }

            // Update color of preview section
            colorList.setSelectedItem(newSelection);

        } else {

            /*
             * Apply new color to selected color of default configuration.
             */

            Color nc = jcc.getColor();
            if (newSelection.equals(LangTool.getString("sa.bg"))) {
                if (!getColorProperty(ColorProperty.BACKGROUND.key()).equals(nc)) {
                    changes.firePropertyChange(this, ColorProperty.BACKGROUND.key(), getColorProperty(ColorProperty.BACKGROUND.key()), nc);
                    setProperty(ColorProperty.BACKGROUND.key(), Integer.toString(nc.getRGB()));
                }
            }

            if (newSelection.equals(LangTool.getString("sa.blue"))) {
                if (!getColorProperty(ColorProperty.BLUE.key()).equals(nc)) {
                    changes.firePropertyChange(this, ColorProperty.BLUE.key(), getColorProperty(ColorProperty.BLUE.key()), nc);
                    setProperty(ColorProperty.BLUE.key(), Integer.toString(nc.getRGB()));
                }
            }

            if (newSelection.equals(LangTool.getString("sa.red"))) {
                if (!getColorProperty(ColorProperty.RED.key()).equals(nc)) {
                    changes.firePropertyChange(this, ColorProperty.RED.key(), getColorProperty(ColorProperty.RED.key()), nc);
                    setProperty(ColorProperty.RED.key(), Integer.toString(nc.getRGB()));
                }
            }

            if (newSelection.equals(LangTool.getString("sa.pink"))) {
                if (!getColorProperty(ColorProperty.PINK.key()).equals(nc)) {
                    changes.firePropertyChange(this, ColorProperty.PINK.key(), getColorProperty(ColorProperty.PINK.key()), nc);
                    setProperty(ColorProperty.PINK.key(), Integer.toString(nc.getRGB()));
                }
            }

            if (newSelection.equals(LangTool.getString("sa.green"))) {
                if (!getColorProperty(ColorProperty.GREEN.key()).equals(nc)) {
                    changes.firePropertyChange(this, ColorProperty.GREEN.key(), getColorProperty(ColorProperty.GREEN.key()), nc);
                    setProperty(ColorProperty.GREEN.key(), Integer.toString(nc.getRGB()));
                }
            }

            if (newSelection.equals(LangTool.getString("sa.turq"))) {
                if (!getColorProperty(ColorProperty.TURQUOISE.key()).equals(nc)) {
                    changes.firePropertyChange(this, ColorProperty.TURQUOISE.key(), getColorProperty(ColorProperty.TURQUOISE.key()), nc);
                    setProperty(ColorProperty.TURQUOISE.key(), Integer.toString(nc.getRGB()));
                }
            }

            if (newSelection.equals(LangTool.getString("sa.yellow"))) {
                if (!getColorProperty(ColorProperty.YELLOW.key()).equals(nc)) {
                    changes.firePropertyChange(this, ColorProperty.YELLOW.key(), getColorProperty(ColorProperty.YELLOW.key()), nc);
                    setProperty(ColorProperty.YELLOW.key(), Integer.toString(nc.getRGB()));
                }
            }

            if (newSelection.equals(LangTool.getString("sa.white"))) {
                if (!getColorProperty(ColorProperty.WHITE.key()).equals(nc)) {
                    changes.firePropertyChange(this, ColorProperty.WHITE.key(), getColorProperty(ColorProperty.WHITE.key()), nc);
                    setProperty(ColorProperty.WHITE.key(), Integer.toString(nc.getRGB()));
                }
            }

            if (newSelection.equals(LangTool.getString("sa.guiField"))) {
                if (!getColorProperty(ColorProperty.GUI_FIELD.key()).equals(nc)) {
                    changes.firePropertyChange(this, ColorProperty.GUI_FIELD.key(), getColorProperty(ColorProperty.GUI_FIELD.key()), nc);
                    setProperty(ColorProperty.GUI_FIELD.key(), Integer.toString(nc.getRGB()));
                }
            }

            if (newSelection.equals(LangTool.getString("sa.cursorColor"))) {
                if (!getColorProperty(ColorProperty.CURSOR.key()).equals(nc)) {
                    changes.firePropertyChange(this, ColorProperty.CURSOR.key(), getColorProperty(ColorProperty.CURSOR.key()), nc);
                    setProperty(ColorProperty.CURSOR.key(), Integer.toString(nc.getRGB()));
                }
            }

            if (newSelection.equals(LangTool.getString("sa.columnSep"))) {
                if (!getColorProperty(ColorProperty.SEPARATOR.key()).equals(nc)) {
                    changes.firePropertyChange(this, ColorProperty.SEPARATOR.key(), getColorProperty(ColorProperty.SEPARATOR.key()), nc);
                    setProperty(ColorProperty.SEPARATOR.key(), Integer.toString(nc.getRGB()));
                }
            }

            if (newSelection.equals(LangTool.getString("sa.hexAttrColor"))) {
                if (!getColorProperty(ColorProperty.HEX_ATTR.key()).equals(nc)) {
                    changes.firePropertyChange(this, ColorProperty.HEX_ATTR.key(), getColorProperty(ColorProperty.HEX_ATTR.key()), nc);
                    setProperty(ColorProperty.HEX_ATTR.key(), Integer.toString(nc.getRGB()));
                }
            }

        }

    }

    private void loadSchemas(JComboBox schemas) {

        schemaProps = new Properties();
        URL file = null;

        try {
            ClassLoader cl = this.getClass().getClassLoader();
            if (cl == null) cl = ClassLoader.getSystemClassLoader();
            file = cl.getResource("tn5250jSchemas.properties");
            schemaProps.load(file.openStream());
        } catch (Exception e) {
            System.err.println(e);
        }

        int numSchemas = Integer.parseInt((String)schemaProps.get("schemas"));
        Schema s = null;
        String prefix = "";
        Schema[] schemaArray = new Schema[numSchemas];
        for (int x = 1; x <= numSchemas; x++) {
            s = new Schema();
            prefix = "schema" + x;
            s.setDescription((String)schemaProps.get(prefix + ".title"));
            s.setColorBg(getSchemaProp(prefix + ".colorBg"));
            s.setColorRed(getSchemaProp(prefix + ".colorRed"));
            s.setColorTurq(getSchemaProp(prefix + ".colorTurq"));
            s.setColorCursor(getSchemaProp(prefix + ".colorCursor"));
            s.setColorGuiField(getSchemaProp(prefix + ".colorGUIField"));
            s.setColorWhite(getSchemaProp(prefix + ".colorWhite"));
            s.setColorYellow(getSchemaProp(prefix + ".colorYellow"));
            s.setColorGreen(getSchemaProp(prefix + ".colorGreen"));
            s.setColorPink(getSchemaProp(prefix + ".colorPink"));
            s.setColorBlue(getSchemaProp(prefix + ".colorBlue"));
            s.setColorSeparator(getSchemaProp(prefix + ".colorSep"));
            s.setColorHexAttr(getSchemaProp(prefix + ".colorHexAttr"));
            schemaArray[x - 1] = s;
        }

        Arrays.sort(schemaArray, new SchemaComparator());

        schemas.addItem(LangTool.getString("sa.colorDefault"));

        for (int x = 1; x <= numSchemas; x++) {
            schemas.addItem(schemaArray[x - 1]);
        }
    }

    private int getSchemaProp(String key) {

        if (schemaProps.containsKey(key)) {

            return Integer.parseInt((String)schemaProps.get(key));

        } else {
            return 0;
        }

    }

    class SchemaComparator implements Comparator<Schema> {

        public int compare(Schema schema1, Schema schema2) {

            if (schema1 == null && schema2 == null) {
                return 0;
            } else if (schema1 == null) {
                return -1;
            } else if (schema2 == null) {
                return 1;
            } else {

                String name1 = schema1.getDescription();
                String name2 = schema2.getDescription();

                return name1.compareTo(name2);
            }
        }
    }

    class Schema {

        public String toString() {

            return description;

        }

        public void setDescription(String desc) {

            description = desc;
        }

        public String getDescription() {

            if (description == null) {
                return "";
            }

            return description;
        }

        public void setColorBg(int color) {

            bg = new Color(color);
        }

        public Color getColorBg() {

            return bg;
        }

        public void setColorBlue(int color) {

            blue = new Color(color);
        }

        public Color getColorBlue() {

            return blue;
        }

        public void setColorRed(int color) {

            red = new Color(color);
        }

        public Color getColorRed() {

            return red;
        }

        public void setColorPink(int color) {

            pink = new Color(color);
        }

        public Color getColorPink() {

            return pink;
        }

        public void setColorGreen(int color) {

            green = new Color(color);
        }

        public Color getColorGreen() {

            return green;
        }

        public void setColorTurq(int color) {

            turq = new Color(color);
        }

        public Color getColorTurq() {

            return turq;
        }

        public void setColorYellow(int color) {

            yellow = new Color(color);
        }

        public Color getColorYellow() {

            return yellow;
        }

        public void setColorWhite(int color) {

            white = new Color(color);
        }

        public Color getColorWhite() {

            return white;
        }

        public void setColorGuiField(int color) {

            gui = new Color(color);
        }

        public Color getColorGuiField() {

            return gui;
        }

        public void setColorCursor(int color) {

            cursor = new Color(color);
        }

        public Color getColorCursor() {

            return cursor;
        }

        public void setColorSeparator(int color) {

            columnSep = new Color(color);
        }

        public Color getColorSeparator() {

            return columnSep;
        }

        public void setColorHexAttr(int color) {

            hexAttr = new Color(color);
        }

        public Color getColorHexAttr() {

            return hexAttr;
        }

        private String description;
        private Color bg;
        private Color blue;
        private Color red;
        private Color pink;
        private Color green;
        private Color turq;
        private Color white;
        private Color yellow;
        private Color gui;
        private Color cursor;
        private Color columnSep;
        private Color hexAttr;
    }
}