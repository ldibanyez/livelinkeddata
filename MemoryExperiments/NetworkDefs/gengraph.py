import argparse
import networkx as nx

# Enum emulation
# http://stackoverflow.com/questions/36932/how-can-i-represent-an-enum-in-python
def enum(**enums):
    return type('Enum', (), enums)

Graphtype = enum(RANDOM='random',
        BARABASI="barabasi",
        ERDOS="erdos",
        RING="ring")

def gen_graph(n,d,t):
    #as for def of density
    numedges= d*n*(n-1)
    if t == Graphtype.RANDOM:
        return gen_random(n,numedges)
    elif t == Graphtype.BARABASI:
        return gen_barabasi(n,numedges)
    elif t == Graphtype.ERDOS:
        return gen_erdos(n,numedges)
    elif t == Graphtype.RING:
        return gen_ring(n)
    else:
        raise ValueError("Bad graph type "+ t)

def gen_ring(n):
    return nx.cycle_graph(n,create_using=nx.DiGraph())

def gen_random(n,m):
    return nx.gnm_random_graph(n,m,directed=True)

def gen_erdos(n,m):
    """
    The expected number of edges in an Erdos-Renyi graph
    is m = p*n*(n-1)/2
    """
    p = 2*m/(n*(n-1))
    return nx.erdos_renyi_graph(n,p,directed=True)

def gen_barabasi(n,m):
    """
    Problem, it only generates an undirected graph
    """

#Snap has now a python version, maybe add the forest fire?
#But it will take some time to read the paper and find the
# number of expected edges.

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Generate a network')
    parser.add_argument('graphtype',help='Type of the Graph')
    parser.add_argument('numnodes',type=int,
            help='Number of nodes of the network')
    parser.add_argument('density',type=float,help='Density of the network')
    args = parser.parse_args() 
    graph = gen_graph(args.numnodes,args.density,args.graphtype)
    if args.graphtype == Graphtype.RING:
        filename = args.graphtype+"-"+str(args.numnodes)+".edgelist"
    else:
        filename = (args.graphtype
                +"-"+str(args.numnodes)
                +"-"+str(args.density)
                +".edgelist")

    nx.write_edgelist(graph,filename)
            



