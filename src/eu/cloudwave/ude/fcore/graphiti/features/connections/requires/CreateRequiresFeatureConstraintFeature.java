package eu.cloudwave.ude.fcore.graphiti.features.connections.requires;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.impl.AbstractCreateConnectionFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;

import eu.cloudwave.ude.fcore.graphiti.FCORE.RequiresFeatureConstraint;
import eu.cloudwave.ude.fcore.graphiti.FCORE.FCOREFactory;
import eu.cloudwave.ude.fcore.graphiti.FCORE.Feature;

public class CreateRequiresFeatureConstraintFeature extends
		AbstractCreateConnectionFeature {

	public CreateRequiresFeatureConstraintFeature (IFeatureProvider fp) {
        // provide name and description for the UI, e.g. the palette
        super(fp, "Requires Constraint", "Create Requires Constraint");
    }
 
    public boolean canCreate(ICreateConnectionContext context) {
        // return true if both anchors belong to a Feature
        // and those Features are not identical
        Feature source = getFeature(context.getSourceAnchor());
        Feature target = getFeature(context.getTargetAnchor());
        if (source != null && target != null && source != target) {
            return true;
        }
        return false;
    }
 
    public boolean canStartConnection(ICreateConnectionContext context) {
        // return true if start anchor belongs to a Feature
        if (getFeature(context.getSourceAnchor()) != null) {
            return true;
        }
        return false;
    }
 
    public Connection create(ICreateConnectionContext context) {
        Connection newConnection = null;
 
        // get Features which should be connected
        Feature source = getFeature(context.getSourceAnchor());
        Feature target = getFeature(context.getTargetAnchor());
 
        if (source != null && target != null) {
            // create new business object 
            RequiresFeatureConstraint requires = createRequiresFeatureConstraint(source, target);
            // add connection for business object
            AddConnectionContext addContext =
                new AddConnectionContext(context.getSourceAnchor(), context
                    .getTargetAnchor());
            addContext.setNewObject(requires);
            newConnection =
                (Connection) getFeatureProvider().addIfPossible(addContext);
        }
        
        return newConnection;
    }
 
    /**
     * Returns the Feature belonging to the anchor, or null if not available.
     */
    private Feature getFeature(Anchor anchor) {
        if (anchor != null) {
            Object object =
                getBusinessObjectForPictogramElement(anchor.getParent());
            if (object instanceof Feature) {
                return (Feature) object;
            }
        }
        return null;
    }
 
    /**
    * Creates a RequiresFeatureConstraint between two Features.
    */
    private RequiresFeatureConstraint createRequiresFeatureConstraint(Feature source, Feature target) {
    	RequiresFeatureConstraint requires = FCOREFactory.eINSTANCE.createRequiresFeatureConstraint();
        requires.setFeatureStart(source);
        requires.setFeatureEnd(target);
        getDiagram().eResource().getContents().add(requires);
        return requires;
   }

}
