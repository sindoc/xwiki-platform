/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.security;

import java.util.Deque;
import java.util.LinkedList;

import org.xwiki.model.EntityType;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.model.reference.SpaceReference;
import org.xwiki.model.reference.WikiReference;

/**
 * A reference to a security entity.
 *
 * @see {@link SecurityReferenceFactory}
 * @version $Id$
 */
public class SecurityReference extends EntityReference
{
    /** EntityType for the main wiki. */
    public static final EntityType FARM = null;

    /** Serialization identifier. */
    private static final long serialVersionUID = 1L;

    /** Main wiki reference. */
    private SecurityReference mainWikiReference;

    /** Original reference represented by this reference. */
    private EntityReference originalReference;

    /**
     * @param reference the reference to the main wiki that will be converted to a security reference
     */
    SecurityReference(EntityReference reference)
    {
        super(reference);
        this.originalReference = reference;
        this.mainWikiReference = this;
    }

    /**
     * @param reference the reference to an entity that will be converted to a security reference
     * @param mainWiki the security reference to the main wiki.
     */
    SecurityReference(EntityReference reference, SecurityReference mainWiki)
    {
        super(reference);
        this.originalReference = reference;
        this.mainWikiReference = mainWiki;
    }

    /**
     * @return the parent reference of this security reference. For a reference to a subwiki which do not have a 
     *         parent by definition, this returns the main wiki reference.
     */
    public SecurityReference getParentSecurityReference()
    {
        EntityReference parent = getParent();

        if (parent == null && this.getType() == EntityType.WIKI && !this.equals(this.mainWikiReference)) {
            return this.mainWikiReference;
        }
        return (parent != null && !(parent instanceof SecurityReference))
            ? new SecurityReference(parent, this.mainWikiReference)
            : (SecurityReference) parent;
    }

    /**
     * @return the reversed reference chain using {@link #getParentSecurityReference}.
     */
    public Deque<SecurityReference> getReversedSecurityReferenceChain()
    {
        Deque<SecurityReference> referenceList = new LinkedList<SecurityReference>();
        SecurityReference reference = this;
        do {
            referenceList.push(reference);
            reference = reference.getParentSecurityReference();
        } while (reference != null);
        return referenceList;
    }

    /**
     * @return the entity reference type, but for the main wiki, return {@link Right.FARM}.
     */
    public EntityType getSecurityType()
    {
        EntityType type = getType();
        if (type != EntityType.WIKI || !this.equals(this.mainWikiReference)) {
            return type;
        }
        return FARM;
    }

    /**
     * @return the original reference used when this security reference was built.
     */
    public EntityReference getOriginalReference()
    {
        return this.originalReference;
    }

    /**
     * @return the original wiki reference used when this security reference was built. Null if this is not
     *         the reference to a wiki.
     */
    public WikiReference getOriginalWikiReference()
    {
        return (this.getType() == EntityType.WIKI)
            ? (this.originalReference instanceof WikiReference)
                ? (WikiReference) this.originalReference
                : new WikiReference(this.originalReference)
            : null;
    }

    /**
     * @return the original space reference used when this security reference was built. Null if this is not
     *         the reference to a space.
     */
    public SpaceReference getOriginalSpaceReference()
    {
        return (this.getType() == EntityType.SPACE)
            ? (this.originalReference instanceof SpaceReference)
                ? (SpaceReference) this.originalReference
                : new SpaceReference(this.originalReference)
            : null;
    }

    /**
     * @return the original document reference used when this security reference was built. Null if this is not
     *         the reference to a document.
     */
    public DocumentReference getOriginalDocumentReference()
    {
        return (this.getType() == EntityType.DOCUMENT)
            ? (this.originalReference instanceof DocumentReference)
                ? (DocumentReference) this.originalReference
                : new DocumentReference(this.originalReference)
            : null;
    }
}