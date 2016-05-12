import networkx as nx
import gengraph as gg
import argparse

def draw(G,filename):
    toDraw = nx.to_agraph(G)
    #toDraw.layout()
    toDraw.draw(filename,prog='dot')



if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Prints a graph in png format')
    parser.add_argument('graphfile',help='Edge list of the graph')
    parser.add_argument('format',help='Output format (png,svg)')
    args = parser.parse_args() 

    graph = nx.read_edgelist(args.graphfile,create_using=nx.DiGraph())
    draw(graph,args.graphfile.replace('.edgelist','.'+args.format))


