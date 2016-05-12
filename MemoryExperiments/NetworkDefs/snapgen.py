import snap
import argparse

def enum(**enums):
    return type('Enum', (), enums)

Graphtype = enum(RANDOM='random',
        FORESTFIRE="forestfire",
        ERDOS="erdos",
        RING="ring",
        COMPLETE="complete")

def density_directed(G):
    """ It seems that snap does not implements
    it natively
    """
    n = G.GetNodes()
    m = G.GetEdges()
    return m/float(n*(n-1))

def reverse(G):
    """Convenience method for returning the reverse of a directed graph
    Strangely, not a method in snap
    """
    if isinstance(G,snap.PUNGraph):
        return G
    reverse = snap.TNGraph.New(G.GetNodes(),G.GetEdges())
    for node in G.Nodes():
        reverse.AddNode(node.GetId())
    for edge in G.Edges():
        reverse.AddEdge(edge.GetDstNId(),edge.GetSrcNId())
    return reverse


def gen_erdos(n,d):
    """ Generates an erdos-renyi graph
        with given density
    """
    m = int(d*n*(n-1))
    #speaking about mnemonics...
    erdos = snap.GenRndGnm(snap.PNGraph,n,m)
    return erdos

def gen_forest_fire(n,d,backburn=0.3,tolerance=0.05):
    """ Generates a ForestFire directed graph with a given density
        It generates a graph int the space of the forward burning probability
        while fixing the backward burning probability
        Why 0.3? Is the value fixed in the plots of the Leskovec et al. paper
        at ACM KDD 2007
        Parameters:
        n = Number of nodes
        d = density
        backburn = Fixed Backward burning probability
        tolerance = Percentage of deviation of the result from the d that we can
        tolerate
    """
    #We tolerate 5% of error
    error = d * tolerance
    forwardprob = 0.01
    forestfire = snap.GenForestFire(n,forwardprob,backburn)
    currentdensity = density_directed(forestfire)
    while d - currentdensity > error:
        forwardprob += 0.01
        forestfire = snap.GenForestFire(n,forwardprob,0.3)
        currentdensity = density_directed(forestfire)
    if currentdensity - d > error:
        raise RuntimeError('Could not generate a graph with the given parameters \n'+
                'Run again, raise tolerance or change graph size')
    return forestfire

def gen_graph(t,n,d):
    if t == Graphtype.RANDOM:
        return gen_random(n,numedges)
    elif t == Graphtype.FORESTFIRE:
        #We return the reverse of the forest fire
        # to match the data flow direction
        try:
            return reverse(gen_forest_fire(n,d))
        except RuntimeError:
            return gen_graph(t,n,d)
    elif t == Graphtype.ERDOS:
        return gen_erdos(n,d)
    elif t == Graphtype.RING:
        return gen_ring(n)
    elif t == Graphtype.COMPLETE:
        return snap.GenFull(snap.PNGraph,n)
    else:
        raise ValueError("Bad graph type "+ t)

def gen_instances(numins,t,n,d=None):
    if (t == Graphtype.RING or 
        t == Graphtype.COMPLETE):
        basename=t+"-"+str(n)+"-"
    else:
        basename=t+"-"+str(n)+"-"+str(d)+"-"
    for i in range(numins):
        graph = gen_graph(t,n,d)
        filename = basename + str(i)+".edgelist"
        snap.SaveEdgeList(graph,filename,
                 'Density: '+str(density_directed(graph)))


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Generate a network')
    parser.add_argument('numinstances',type=int,
            help='Number of instances to generate')
    parser.add_argument('graphtype',help='Type of the Graph')
    parser.add_argument('numnodes',type=int,
            help='Number of nodes of the network')
    parser.add_argument('density',type=float,help='Density of the network')
    args = parser.parse_args() 
    gen_instances(args.numinstances,args.graphtype,
                  args.numnodes,args.density)
