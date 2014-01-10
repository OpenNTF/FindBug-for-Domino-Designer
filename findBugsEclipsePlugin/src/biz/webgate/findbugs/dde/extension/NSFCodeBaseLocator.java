/*
 * FindBugs Domino Designer Intergration
 * Copyright (C) 2014, Christian Güdemann
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package biz.webgate.findbugs.dde.extension;

import java.io.IOException;

import edu.umd.cs.findbugs.classfile.ICodeBase;
import edu.umd.cs.findbugs.classfile.ICodeBaseLocator;
import edu.umd.cs.findbugs.classfile.ResourceNotFoundException;
import edu.umd.cs.findbugs.classfile.impl.ClassFactory;

public class NSFCodeBaseLocator implements ICodeBaseLocator {

    private final String m_Path;

    public NSFCodeBaseLocator(String path) {
        super();
        m_Path = path;
    }

    @Override
    public ICodeBase openCodeBase() throws IOException, ResourceNotFoundException {
        return new NSFCodeBase(this, m_Path);
    }

    @Override
    public ICodeBaseLocator createRelativeCodeBaseLocator(String relativePath) {
        return ClassFactory.instance().createFilesystemCodeBaseLocator(relativePath);
    }

}
