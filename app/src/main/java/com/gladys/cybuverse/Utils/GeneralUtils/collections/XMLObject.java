package com.gladys.cybuverse.Utils.GeneralUtils.collections;

import java.util.ArrayList;
import java.util.List;

import static com.gladys.cybuverse.Utils.GeneralUtils.Funcs.format;

public class XMLObject {

    private String PARENTTAG;
    private String RAWCHILD;
    private Dictionary<Object, Object> PROPERTIES = new Dictionary<>();
    private List<XMLObject> CHILDREN = new ArrayList<>();

    public XMLObject() {
    }

    public XMLObject(String parentTag) {
        setParentTAG(parentTag);
    }

    private boolean isString(String line) {
        line = line.trim();
        return line.charAt(0) == '"' && line.charAt(line.length() - 1) == '"';
    }

    public String getParentTAG() {
        return PARENTTAG;
    }

    public void setParentTAG(String parentTag) {
        PARENTTAG = parentTag;
    }

    public XMLObject addProperty(Object propertyName, Object propertyValue) {
        PROPERTIES.set(propertyName, propertyValue);
        return this;
    }

    public Dictionary getProperties() {
        return PROPERTIES;
    }

    public XMLObject addChild(XMLObject child) {
        CHILDREN.add(child);
        return this;
    }

    public XMLObject addChildren(XMLObject... children) {
        for (XMLObject child : children) {
            addChild(child);
        }
        return this;
    }

    public void setProperty(Dictionary property) {
        PROPERTIES = property;
    }

    public void setChildren(List<XMLObject> children) {
        CHILDREN = children;
    }

    public XMLObject setRawChild(String rawChild) {
        RAWCHILD = rawChild;
        return this;
    }

    public List<XMLObject> getALLChildren() {
        return CHILDREN;
    }

    public List<XMLObject> getChildrenWithTAG(String childTagName) {
        List<XMLObject> MATCHLIST = new ArrayList<>();
        for (XMLObject child : CHILDREN) {
            if (child.getParentTAG().equals(childTagName)) {
                MATCHLIST.add(child);
            }
        }
        return MATCHLIST;
    }

    public List<XMLObject> getSubChildrenWithTAG(String childTagName) {
        List<XMLObject> MATCHLIST = new ArrayList<>();
        for (XMLObject child : CHILDREN) {
            if (child.getParentTAG().equals(childTagName)) {
                MATCHLIST.add(child);
            }
            List CHILDSMATCHLIST = child.getSubChildrenWithTAG(childTagName);
            MATCHLIST.addAll(CHILDSMATCHLIST);
        }
        return MATCHLIST;
    }

    public Object getPropertyValue(String propertyName) {
        return PROPERTIES.get(propertyName);
    }

    public boolean hasProperty(String propertyName) {
        for (Object property : PROPERTIES.keys()) {
            if (property == propertyName) {
                return true;
            }
        }
        return false;
    }

    public boolean hasChild(XMLObject object) {
        for (XMLObject child : CHILDREN) {
            if (child == object) {
                return true;
            }
        }
        return false;
    }

    public boolean hasChildWithTAG(String childTagName) {
        for (XMLObject child : CHILDREN) {
            if (child.getParentTAG().equals(childTagName)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasSubChildWithTAG(String childTagName) {
        return containsSubChild(childTagName) != null;
    }

    public boolean hasSubChild(XMLObject child) {
        return containsSubChild(child) != null;
    }

    public void replaceAllChildrenWithTAG(String childTagName, String newChildTagName) {
        if (hasChildWithTAG(childTagName)) {
            for (XMLObject child : CHILDREN) {
                if (child.getParentTAG().equals(childTagName)) {
                    int index = CHILDREN.indexOf(child);
                    CHILDREN.remove(index);
                    child.setParentTAG(newChildTagName);
                    CHILDREN.add(index, child);
                }
            }
        }
    }

    public void replaceAllSubChildrenWithTAG(String childTagName, String newChildTagName) {
        if (hasSubChildWithTAG(childTagName)) {
            for (XMLObject child : CHILDREN) {
                if (child.getParentTAG().equals(childTagName)) {
                    int index = CHILDREN.indexOf(child);
                    CHILDREN.remove(index);
                    child.setParentTAG(newChildTagName);
                    CHILDREN.add(index, child);
                }
                child.replaceAllSubChildrenWithTAG(childTagName, newChildTagName);
            }
        }
    }

    public void removeAllChildrenWithTAG(String childTagName) {
        List<XMLObject> DATA = new ArrayList<>(CHILDREN);
        for (XMLObject child : DATA) {
            if (child.getParentTAG().equals(childTagName)) {
                CHILDREN.remove(child);
            }
        }
    }

    public void removeAllSubChildrenWithTAG(String childTagName) {
        List<XMLObject> DATA = new ArrayList<>(CHILDREN);

        for (XMLObject child : DATA) {
            if (child.getParentTAG().equals(childTagName)) {
                CHILDREN.remove(child);
            } else {
                if (child.hasSubChildWithTAG(childTagName)) {
                    child.removeAllSubChildrenWithTAG(childTagName);
                }
            }
        }
    }

    private XMLObject containsSubChild(String childTagName) {
        for (XMLObject child : CHILDREN) {
            if (child.getParentTAG().equals(childTagName)) {
                return child;
            } else {
                if (child.containsSubChild(childTagName) != null) {
                    return child;
                }
            }
        }
        return null;
    }

    private XMLObject containsSubChild(XMLObject object) {
        for (XMLObject child : CHILDREN) {
            if (child == object) {
                return child;
            } else {
                if (child.containsSubChild(object) != null) {
                    return child;
                }
            }
        }
        return null;
    }

    private String getPropertyString() {
        StringBuilder property = new StringBuilder();

        Enumerator<KeyValuePair<Object, Object>> enumerator = PROPERTIES.getEnumerator();

        while (enumerator.hasNext()) {
            KeyValuePair x = enumerator.getNext();
            property.append(" ").append(x.getKey().toString()).append("=\"").append(x.getValue().toString()).append('"');
        }
        if (property.length() > 0) {
            property.append(" ");
        }

        return property.toString();
    }

    private String getChildrenString() {

        StringBuilder children = new StringBuilder();

        if (RAWCHILD != null) {
            children.append("\n").append(RAWCHILD).append("\n");
        } else {
            for (XMLObject x : CHILDREN) {
                children.append("\n").append(x.toString());
            }

            if (children.length() > 0) {
                children.append("\n");
            }
        }

        return children.toString();
    }

    public boolean isEmpty() {
        return PARENTTAG == null;
    }

    public void reset() {
        PARENTTAG = null;
        CHILDREN.clear();
        PROPERTIES.clear();
        RAWCHILD = null;
    }

    public void resetChildren() {
        CHILDREN.clear();
        RAWCHILD = null;
    }

    public void resetProperties() {
        PROPERTIES.clear();
    }

    public String toString() {
        if (PARENTTAG != null) {
            return format("<<1><2>><3></<1>>", getParentTAG(), getPropertyString(), getChildrenString());
        }
        return null;
    }
}
