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

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;

import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import edu.umd.cs.findbugs.classfile.ICodeBase;
import edu.umd.cs.findbugs.classfile.ICodeBaseEntry;
import edu.umd.cs.findbugs.classfile.InvalidClassFileFormatException;
import edu.umd.cs.findbugs.classfile.ResourceNotFoundException;
import edu.umd.cs.findbugs.classfile.analysis.ClassNameAndSuperclassInfo;
import edu.umd.cs.findbugs.classfile.engine.ClassParser;
import edu.umd.cs.findbugs.classfile.engine.ClassParserInterface;
import edu.umd.cs.findbugs.io.IO;

public class NSFCodeBaseEntry implements ICodeBaseEntry {

    public static final String WEB_CONTENT_WEB_INF_CLASSES = "WebContent\\WEB-INF\\classes";
    public static final String WEBSERVICE_CLASSES = "bin";

    private final IFile m_ResourceFile;

    private final IFolder m_Folder;

    private final NSFCodeBase m_CodeBase;

    public NSFCodeBaseEntry(IFile resourceFile, NSFCodeBase nsfCodeBase, IFolder ifCurrent) {
        super();
        m_ResourceFile = resourceFile;
        m_CodeBase = nsfCodeBase;
        m_Folder = ifCurrent;
    }

    @Override
    public String getResourceName() {
        String strResult=null;
        String strPath = m_Folder.getFullPath().toOSString();

        if (strPath.contains(WEB_CONTENT_WEB_INF_CLASSES)){
            int nPos = strPath.indexOf(WEB_CONTENT_WEB_INF_CLASSES) + WEB_CONTENT_WEB_INF_CLASSES.length() + 1;
            strPath = strPath.substring(nPos);
            strPath = strPath.replaceAll("\\\\", "/");
            strResult = strPath + "/" + m_ResourceFile.getName();
        }
        else {
            int nPos = strPath.indexOf(WEBSERVICE_CLASSES) + WEBSERVICE_CLASSES.length() + 1;
            if (nPos < strPath.length()) {
                strPath = strPath.substring(nPos);
                strPath = strPath.replaceAll("\\\\", "/");
                strResult = strPath + "/" + m_ResourceFile.getName();
            }
            else {
                strResult = m_ResourceFile.getName();
            }

        }
        return strResult;
    }

    @Override
    public int getNumBytes() {
        try {
            InputStream in = openResource();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int next = in.read();
            while (next > -1) {
                bos.write(next);
                next = in.read();
            }
            bos.flush();
            in.close();
            byte[] result = bos.toByteArray();
            return result.length;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 1;
    }

    @Override
    public InputStream openResource() throws IOException {
        try {
            return m_ResourceFile.getContents();
        } catch (Exception es) {
            es.printStackTrace();
        }
        return null;
    }

    @Override
    public ICodeBase getCodeBase() {
        return m_CodeBase;
    }

    @Override
    public ClassDescriptor getClassDescriptor() throws ResourceNotFoundException, InvalidClassFileFormatException {
        DataInputStream in = null;
        try {
            try {
                in = new DataInputStream(this.openResource());
                ClassParserInterface classParser = new ClassParser(in, null, this);
                ClassNameAndSuperclassInfo.Builder builder = new ClassNameAndSuperclassInfo.Builder();

                classParser.parse(builder);

                return builder.build().getClassDescriptor();
            } finally {
                if (in != null) {
                    IO.close(in);
                }
            }
        } catch (IOException e) {
            throw new ResourceNotFoundException(this.getResourceName());
        }

    }

    @Override
    public void overrideResourceName(String resourceName) {
        //TODO: Need code?
    }

}
