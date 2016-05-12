try:
    import xml.etree.cElementTree as ET
except ImportError:
    import xml.etree.ElementTree as ET
import networkx as NX
import gengraph as gg
import fileinput
import argparse
from os import path
from xml.dom import minidom

def prettify(elem):
    """Return a pretty-printed XML string for the Element. 
    Kudos to StackOverFlow
    http://stackoverflow.com/questions/17402323/use-xml-etree-elementtree-to-write-out-nicely-formatted-xml-files
    """
    rough_string = ET.tostring(elem, 'utf-8')
    reparsed = minidom.parseString(rough_string)
    return reparsed.toprettyxml()

def gen_dynamic():
    noddyn = ET.Element('dynamic')
    noddynins = ET.SubElement(noddyn,'insert')
    noddynins.text = str(0.0)
    noddyndel = ET.SubElement(noddyn,'delete')
    noddyndel.text = str(0.0)
    return noddyn


def gen_views():
    views = []
    nodview = ET.Element('view')
    nodsubject = ET.SubElement(nodview,'subject')
    nodpredicate = ET.SubElement(nodview,'predicate')
    nodobject = ET.SubElement(nodview,'object')
    views.append(nodview)
    return views

def gen_sources(G,node):
    """ Generates the source elements for node
    an edge 1 -> node in the graph means that a
    source element
    <source id=participant1>
    </source>
    will be created
    """
    sources = []
    for edge in G.in_edges_iter([node]):
        nodsource = ET.Element('source')
        nodsource.set('id','participant'+str(edge[0]))
        for view in gen_views():
            nodsource.append(view)
        sources.append(nodsource)
    return sources

def gen_network(graph,machines,basedata):
    """ Generates an LLD network from a graph
        distributing participants in a list of machines
    """
    network = ET.Element('network')
    #network.set('type',graphtype)
    network.set('participants',str(graph.number_of_nodes()))
    network.set('edges',str(graph.size()))
    network.set('density',str(NX.density(graph)))

    network.set('connected',str(NX.is_weakly_connected(graph)))
    network.set('stronglyconnected',str(NX.is_strongly_connected(graph)))

    for node in graph.nodes_iter():
        nodelement = ET.SubElement(network,'participant')
        nodelement.set('id','participant'+str(node))
        hostelem = ET.SubElement(nodelement,'host')
        #hostelem.text = 'node'+str(int(node) % len(machines))
        hostelem.text = machines[int(node) % len(machines)]
        portelem = ET.SubElement(nodelement,'port')
        portelem.text = str(20500+int(node))
        baseelem = ET.SubElement(nodelement,'basedata')
        baseelem.text = basedata
        nodelement.append(gen_dynamic())
        for source in gen_sources(graph,node):
            nodelement.append(source)
    return network



if __name__ == '__main__':
    parser = argparse.ArgumentParser(
        description='Generate an LLD network from a graph')
    parser.add_argument('graphfile',
        help='Edge List of the directed graph to LLD-ify')
    parser.add_argument('machinesfile',
            help="""File containing the names of the machines where the LLD will
            be distributed, one per line""")
    parser.add_argument('basedata',help='URI of the basedata to put in all nodes')
    args = parser.parse_args() 
    with open(args.machinesfile) as f:
        machines = [line.strip() for line in f]
    graph = NX.read_edgelist(args.graphfile,create_using=NX.DiGraph())
    lldnet = gen_network(graph,machines,args.basedata)
    """
    # Work around to pretty print
    # Again, kudos to StackOverflow
    basename = path.basename(args.graphfile)
    filename=("LLD-"+basename.replace('.edgelist','')+".xml")
    fo = open(filename, "w")
    fo.write(prettify(lldnet))
    fo.close()
    """
    #Hack to add DTD
    prettylist = prettify(lldnet).split('\n',2)
    prettylist.insert(1,"<!DOCTYPE network SYSTEM 'Network.dtd'>")
    for line in prettylist:
        print line
