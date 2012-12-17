/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.web.click.editor.completion;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author Rohan Ranade
 */
public final class CompletionResult {
    private final List<ClickCompletionItem> items;
    private final int anchorOffset;
    private final boolean additionalItems;
    private final String additionalItemsText;
    
    public static final CompletionResult NONE = new CompletionResult(Collections.<ClickCompletionItem>emptyList(), -1, false, null);
    
    public static CompletionResult create(List<ClickCompletionItem> items, int anchorOffset, boolean hasAdditionalItems, String additionalItemsText) {
        if(items.isEmpty()) {
            return CompletionResult.NONE;
        }
        
        return new CompletionResult(items, anchorOffset, hasAdditionalItems, additionalItemsText);
    }
    
    public static CompletionResult create(List<ClickCompletionItem> items, int anchorOffset) {
        return create(items, anchorOffset, false, null);
    }
    
    private CompletionResult(List<ClickCompletionItem> items, int anchorOffset, boolean hasAdditionalItems, String additionalItemsText) {
        this.items = items;
        this.anchorOffset = anchorOffset;
        this.additionalItems = hasAdditionalItems;
        this.additionalItemsText = additionalItemsText;
    }

    public List<ClickCompletionItem> getItems() {
        return items;
    }

    public int getAnchorOffset() {
        return anchorOffset;
    }

    public String getAdditionalItemsText() {
        return additionalItemsText;
    }

    public boolean hasAdditionalItems() {
        return additionalItems;
    }
}
