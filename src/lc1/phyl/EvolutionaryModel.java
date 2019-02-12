package lc1.phyl;

import pal.alignment.Alignment;
import pal.substmodel.SubstitutionModel;
import pal.tree.ParameterizedTree;

public abstract interface EvolutionaryModel
{
  public abstract ParameterizedTree getTree();
  
  public abstract SubstitutionModel getSubstitutionModel();
  
  public abstract Alignment getAlignment();
}


/* Location:              /home/lachlan/Desktop/psilc1.21/psilc.jar!/lc1/phyl/EvolutionaryModel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */