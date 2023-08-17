from operator import concat

class Node():
    def __init__(self, name):
        self.name = name
        self.children = []

    def addChild(self, child):
        childNode = Node(child)
        self.children.append(childNode)
        return childNode

    def getChild(self, name):
        theChild = None
        for child in self.children:
            if child.name == name:
                theChild = child
                break

        return theChild

    def printTree(self, parentPath):
        print(concat(parentPath, "/%s" % self.name))
        for child in self.children:
            child.printTree(concat(parentPath, "/%s" % self.name))

    def contains(self, pathItems):
        name = pathItems[0]

        if len(pathItems) == 1:
            # End of the path. Return either True or False here
            wasFound = False
            for childNode in self.children:
                # See if child name is in this child.
                if name == childNode.name:
                    wasFound = True
                    break

            return wasFound

        # Check the children of this node for the next element of the path
        childPaths = pathItems[1:]
        for childNode in self.children:
            # See if child name is the next item in the path. If not, don't bother going down this path.
            wasFound = False
            if name == childNode.name:
                wasFound = childNode.contains(childPaths)

            if wasFound:
                # break here if the end of the path was eventually found. Otherwise, keep checking the rest of the nodes.
                return wasFound

prodTags = []

testRoot = Node("")

def addChildNodes(parentNode, pathItems):
    if len(pathItems) == 0:
        return
    else:
        name = pathItems.pop(0)
        childNode = parentNode.getChild(name)

        if childNode is None:
            childNode = parentNode.addChild(name)
        addChildNodes(childNode, pathItems)

# with open("C:/tmp/support-07292022/test-dataset.log") as f:
with open("C:/tmp/support-07292022/TEST/wrapper-TEST-everything.log") as f:
    for line in f:
        start = line.find("path='") + 6
        end = line.find("'", start)
        path = line[start:end]
        # testTags.append(path)

        pathItems = path.split("/")
        addChildNodes(testRoot, pathItems)
    
with open("C:/tmp/support-07292022/PROD/wrapper-PROD-everything.log") as f:
    for line in f:
        start = line.find("path='") + 6
        end = line.find("'", start)
        prodTags.append(line[start:end])

print ("PROD tag count=%d" % len(prodTags))

with open('C:/tmp/support-07292022/compare.txt', 'w') as outfile:
    count = 0
    for tag in prodTags:
        isInTest = testRoot.contains(tag.split("/")) 
        if not isInTest:
            outfile.write("Tag '%s' found in PROD tag set but not in TEST tag set\n" % (tag))
        
        count = count + 1

print ("Tag processing complete")