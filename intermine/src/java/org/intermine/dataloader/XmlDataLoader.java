package org.intermine.dataloader;

/*
 * Copyright (C) 2002-2003 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.Iterator;
import java.util.List;
import java.io.InputStream;

import org.intermine.InterMineException;
import org.intermine.model.InterMineObject;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.util.XmlBinding;

/**
 * Provides a method for unmarshalling XML given source  into java
 * business objects then calls store on each.
 * store() is AbstractDataLoader.store().
 *
 * @author Richard Smith
 */

public class XmlDataLoader extends DataLoader
{
    /**
     * @see DataLoader#DataLoader
     */
    public XmlDataLoader(IntegrationWriter iw) {
        super(iw);
    }

    /**
     * Static method to unmarshall business objects from a given xml file and call
     * store on each.
     *
     * @param is access to xml file
     * @throws InterMineException if anything goes wrong with xml or storing
     */
    public void processXml(InputStream is) throws InterMineException {
        try {
            XmlBinding binding = new XmlBinding(iw.getObjectStore().getModel());

            List objects = (List) binding.unmarshal(is);

            Iterator iter = objects.iterator();
            while (iter.hasNext()) {
                iw.store((InterMineObject) iter.next());
            }
        } catch (ObjectStoreException e) {
            throw new InterMineException("Problem with store method", e);
        }
    }
}
