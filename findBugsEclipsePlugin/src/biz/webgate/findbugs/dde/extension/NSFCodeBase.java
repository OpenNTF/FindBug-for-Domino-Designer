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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

import edu.umd.cs.findbugs.classfile.ICodeBaseEntry;
import edu.umd.cs.findbugs.classfile.ICodeBaseIterator;
import edu.umd.cs.findbugs.classfile.ICodeBaseLocator;
import edu.umd.cs.findbugs.classfile.impl.AbstractScannableCodeBase;

public class NSFCodeBase extends AbstractScannableCodeBase {

    private final String m_CodeBase;

    private HashMap<String, NSFCodeBaseEntry> m_Classes;

    public NSFCodeBase(ICodeBaseLocator codeBaseLocator, String strPath) {
        super(codeBaseLocator);
        m_CodeBase = strPath;
        initContent();
        setApplicationCodeBase(true);
    }

    private void initContent() {
        IProject ipCurrent = null;
        String strFolder = "";
        for (IProject proj : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
            IPath ipProject = proj.getLocation();
            ipProject = ipProject.makeAbsolute();
            if (m_CodeBase.contains(ipProject.toOSString())) {
                ipCurrent = proj;
                strFolder = m_CodeBase.substring(ipProject.toOSString().length());
                break;
            }
        }
        if (ipCurrent == null) {
            throw new NullPointerException("ipCurrent not found -> " + m_CodeBase);
        }
        IFolder classes = ipCurrent.getFolder(strFolder);
        List<NSFCodeBaseEntry> entries = getFiles(classes);
        m_Classes = new HashMap<String, NSFCodeBaseEntry>();
        for (NSFCodeBaseEntry ent : entries) {
            m_Classes.put(ent.getResourceName(), ent);
        }
    }

    @Override
    public ICodeBaseIterator iterator() throws InterruptedException {
        final Iterator<NSFCodeBaseEntry> itNSBE = m_Classes.values().iterator();
        return new ICodeBaseIterator() {

            @Override
            public ICodeBaseEntry next() throws InterruptedException {
                return itNSBE.next();
            }

            @Override
            public boolean hasNext() throws InterruptedException {
                return itNSBE.hasNext();
            }
        };
    }

    @Override
    public ICodeBaseEntry lookupResource(String resourceName) {
        return m_Classes.get(resourceName);
    }

    @Override
    public String getPathName() {
        return m_CodeBase;
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

    private List<NSFCodeBaseEntry> getFiles(IFolder ifCurrent) {
        List<NSFCodeBaseEntry> lstRC = new ArrayList<NSFCodeBaseEntry>();
        try {
            for (IResource ir : ifCurrent.members()) {
                if (ir.getType() == IResource.FOLDER) {
                    List<NSFCodeBaseEntry> files = getFiles((IFolder) ir);
                    lstRC.addAll(files);
                } else {
                    if (ir.getType() == IResource.FILE) {
                        lstRC.add(new NSFCodeBaseEntry((IFile) ir, this, ifCurrent));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lstRC;
    }

}
