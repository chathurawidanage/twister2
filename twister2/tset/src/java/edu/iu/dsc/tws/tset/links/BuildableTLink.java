//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.


package edu.iu.dsc.tws.tset.links;

import java.util.Collection;
import java.util.HashSet;

import edu.iu.dsc.tws.api.compute.graph.Edge;
import edu.iu.dsc.tws.api.tset.TBase;
import edu.iu.dsc.tws.tset.TSetGraph;

public interface BuildableTLink extends TBase {

  Edge getEdge();

  default void build(TSetGraph tSetGraph, Collection<? extends TBase> tSets) {

    // filter out the relevant sources out of the predecessors
    HashSet<TBase> relevantSources = new HashSet<>(tSetGraph.getPredecessors(this));
    relevantSources.retainAll(tSets);

    // filter out the relevant sources out of the successors
    HashSet<TBase> relevantTargets = new HashSet<>(tSetGraph.getSuccessors(this));
    relevantTargets.retainAll(tSets);

    for (TBase source : relevantSources) {
      for (TBase target : relevantTargets) {
        String s = source.getId();
        String t = target.getId();

        Edge edge = getEdge();
        edge.setName(edge.getName() + "_" + s + "_" + t);

        tSetGraph.getDfwGraphBuilder().connect(s, t, edge);
      }
    }

  }
}