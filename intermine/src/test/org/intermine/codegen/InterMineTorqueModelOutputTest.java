package org.intermine.codegen;

/*
 * Copyright (C) 2002-2003 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import junit.framework.TestCase;

import java.io.File;
import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Collections;
import java.io.FileReader;
import java.io.FileNotFoundException;

import org.intermine.metadata.Model;
import org.intermine.metadata.ClassDescriptor;
import org.intermine.metadata.AttributeDescriptor;
import org.intermine.metadata.ReferenceDescriptor;
import org.intermine.metadata.CollectionDescriptor;

public class InterMineTorqueModelOutputTest extends TestCase
{
    private String INDENT = ModelOutput.INDENT;
    private String ENDL = ModelOutput.ENDL;
    private Model model;
    private File file;
    private InterMineTorqueModelOutput mo;
    private String uri = "http://www.intermine.org/model/testmodel";

    public InterMineTorqueModelOutputTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        model = new Model("model", uri, new HashSet());
        file = new File("temp.xml");
        mo = new InterMineTorqueModelOutput(model, file);
    }

    public void testProcess() throws Exception {
        ClassDescriptor cld1 = new ClassDescriptor("Class1", null, false, new HashSet(), new HashSet(), new HashSet());
        ClassDescriptor cld2 = new ClassDescriptor("Class2", null, false, new HashSet(), new HashSet(), new HashSet());
        Model model = new Model("model", uri, new LinkedHashSet(Arrays.asList(new Object[] {cld1, cld2})));

        File path = new File("./");
        InterMineTorqueModelOutput mo = new InterMineTorqueModelOutput(model, path);
        mo.process();

        File file = new File("./model-schema.xml");
        FileReader reader = null;
        try {
            reader = new FileReader(file);

        } catch (FileNotFoundException e) {
            fail("file (" + file.getName() + ") not created successfully");
        } finally {
            file.delete();
        }

        try {
            char[] text = new char[1024];
            reader.read(text);

            String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>" + ENDL
            + "<!DOCTYPE database SYSTEM \""
                    + "http://jakarta.apache.org/turbine/dtd/database.dtd\">" + ENDL
            + "<database name=\"\">" + ENDL
            + INDENT + "<table name=\"Class1\">" + ENDL
            + INDENT + INDENT + "<column name=\"OBJECT\" type=\"LONGVARCHAR\"/>" + ENDL
            + INDENT + INDENT + "<column name=\"id\" type=\"INTEGER\" required=\"true\" primaryKey=\"true\"/>" + ENDL
            + INDENT + "</table>" + ENDL
            + INDENT + "<table name=\"Class2\">" + ENDL
            + INDENT + INDENT + "<column name=\"OBJECT\" type=\"LONGVARCHAR\"/>" + ENDL
            + INDENT + INDENT + "<column name=\"id\" type=\"INTEGER\" required=\"true\" primaryKey=\"true\"/>" + ENDL
            + INDENT + "</table>" + ENDL
            + INDENT + "<table name=\"InterMineObject\">" + ENDL
            + INDENT + INDENT + "<column name=\"OBJECT\" type=\"LONGVARCHAR\"/>" + ENDL
            + INDENT + INDENT + "<column name=\"id\" type=\"INTEGER\" required=\"true\" primaryKey=\"true\"/>" + ENDL
            + INDENT + "</table>" + ENDL
            + "</database>" + ENDL;

            // trim text then add a single ENDL
            assertEquals(expected, (new String(text)).trim() + ENDL);
        } finally {
            file.delete();
        }
    }

    public void testGenerateModel() throws Exception {
        ClassDescriptor cld1 = new ClassDescriptor("Class1", null, false, new HashSet(), new HashSet(), new HashSet());
        ClassDescriptor cld2 = new ClassDescriptor("Class2", null, false, new HashSet(), new HashSet(), new HashSet());
        Model model = new Model("model", uri, new LinkedHashSet(Arrays.asList(new Object[] {cld1, cld2})));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>" + ENDL
            + "<!DOCTYPE database SYSTEM \""
            + "http://jakarta.apache.org/turbine/dtd/database.dtd\">" + ENDL
            + "<database name=\"\">" + ENDL
            + INDENT + "<table name=\"Class1\">" + ENDL
            + INDENT + INDENT + "<column name=\"OBJECT\" type=\"LONGVARCHAR\"/>" + ENDL
            + INDENT + INDENT + "<column name=\"id\" type=\"INTEGER\" required=\"true\" primaryKey=\"true\"/>" + ENDL
            + INDENT + "</table>" + ENDL
            + INDENT + "<table name=\"Class2\">" + ENDL
            + INDENT + INDENT + "<column name=\"OBJECT\" type=\"LONGVARCHAR\"/>" + ENDL
            + INDENT + INDENT + "<column name=\"id\" type=\"INTEGER\" required=\"true\" primaryKey=\"true\"/>" + ENDL
            + INDENT + "</table>" + ENDL
            + INDENT + "<table name=\"InterMineObject\">" + ENDL
            + INDENT + INDENT + "<column name=\"OBJECT\" type=\"LONGVARCHAR\"/>" + ENDL
            + INDENT + INDENT + "<column name=\"id\" type=\"INTEGER\" required=\"true\" primaryKey=\"true\"/>" + ENDL
            + INDENT + "</table>" + ENDL
            + "</database>" + ENDL;

        assertEquals(expected, mo.generate(model));
    }

    public void testGenerateClassDescriptorClass() throws Exception {
        ClassDescriptor cld1 = new ClassDescriptor("Class1", null, false, new HashSet(), new HashSet(), new HashSet());
        Model model = new Model("model", uri, new HashSet(Collections.singleton(cld1)));

        String expected = INDENT + "<table name=\"Class1\">" + ENDL
            + INDENT + INDENT + "<column name=\"OBJECT\" type=\"LONGVARCHAR\"/>" + ENDL
            + INDENT + INDENT + "<column name=\"id\" type=\"INTEGER\" required=\"true\" primaryKey=\"true\"/>" + ENDL
            + INDENT + "</table>" + ENDL;

        assertEquals(expected, mo.generate(cld1));
    }

   public void testGenerateClassDescriptorIsInterface() throws Exception {
        ClassDescriptor cld1 = new ClassDescriptor("Interface1", null, true, new HashSet(), new HashSet(), new HashSet());
        Model model = new Model("model", uri, new HashSet(Collections.singleton(cld1)));

        String expected = INDENT + "<table name=\"Interface1\">" + ENDL
            + INDENT + INDENT + "<column name=\"OBJECT\" type=\"LONGVARCHAR\"/>" + ENDL
            + INDENT + INDENT + "<column name=\"id\" type=\"INTEGER\" required=\"true\" primaryKey=\"true\"/>" + ENDL
            + INDENT + "</table>" + ENDL;

        assertEquals(expected, mo.generate(cld1));
    }

    public void testGenerateClassDescriptorFields() throws Exception {
        AttributeDescriptor atd1 = new AttributeDescriptor("name1", "java.lang.String");
        Set atts = new LinkedHashSet(Collections.singleton(atd1));

        ReferenceDescriptor rfd1 = new ReferenceDescriptor("rfd1", "Class2", null);
        Set refs = new LinkedHashSet(Collections.singleton(rfd1));

        // only need simple unidirectionals cods
        CollectionDescriptor cod1 = new CollectionDescriptor("cod1", "Class2", null, true);
        Set cols = new LinkedHashSet(Collections.singleton(cod1));

        ClassDescriptor cld1 = new ClassDescriptor("Class1", null, false, atts, refs, cols);
        ClassDescriptor cld2 = new ClassDescriptor("Class2", null, false, new HashSet(), new HashSet(), new HashSet());
        Model model = new Model("model", uri, new HashSet(Arrays.asList(new Object[] {cld1, cld2})));

        String expected = INDENT + "<table name=\"Class1\">" + ENDL
            + INDENT + INDENT + "<column name=\"OBJECT\" type=\"LONGVARCHAR\"/>" + ENDL
            + INDENT + INDENT + "<column name=\"rfd1Id\" type=\"INTEGER\"/>" + ENDL
            + INDENT + INDENT + "<column name=\"name1\" type=\"LONGVARCHAR\"/>" + ENDL
            + INDENT + INDENT + "<column name=\"id\" type=\"INTEGER\" required=\"true\" primaryKey=\"true\"/>" + ENDL
            + INDENT + "</table>" + ENDL;

        assertEquals(expected, mo.generate(cld1));

        expected = INDENT + "<table name=\"Class1Cod1\">" + ENDL
            + INDENT + INDENT + "<column name=\"Cod1\" type=\"INTEGER\"/>" + ENDL
            + INDENT + INDENT + "<column name=\"Class1\" type=\"INTEGER\"/>" + ENDL
            + INDENT + "</table>" + ENDL;

        assertEquals(expected, mo.generateIndirectionTable(cod1));

    }

    // a ref and attribute descriptor in the superclass
    public void testGenerateClassDescriptorSuperFields() throws Exception {
        AttributeDescriptor atd1 = new AttributeDescriptor("name1", "java.lang.String");
        Set atts1 = new LinkedHashSet(Collections.singleton(atd1));
        ReferenceDescriptor rfd1 = new ReferenceDescriptor("rfd1", "Class2", null);
        Set refs1 = new LinkedHashSet(Collections.singleton(rfd1));
        ClassDescriptor cld1 = new ClassDescriptor("Class1", null, false, atts1, refs1, new HashSet());

        ClassDescriptor cld2 = new ClassDescriptor("Class2", "Class1", false, new HashSet(), new HashSet(), new HashSet());

        Model model = new Model("model", uri, new HashSet(Arrays.asList(new Object[] {cld1, cld2})));


        String expected = INDENT + "<table name=\"Class2\">" + ENDL
            + INDENT + INDENT + "<column name=\"OBJECT\" type=\"LONGVARCHAR\"/>" + ENDL
            + INDENT + INDENT + "<column name=\"rfd1Id\" type=\"INTEGER\"/>" + ENDL
            + INDENT + INDENT + "<column name=\"name1\" type=\"LONGVARCHAR\"/>" + ENDL
            + INDENT + INDENT + "<column name=\"id\" type=\"INTEGER\" required=\"true\" primaryKey=\"true\"/>" + ENDL
            + INDENT + "</table>" + ENDL;

        assertEquals(expected, mo.generate(cld2));

    }

}
