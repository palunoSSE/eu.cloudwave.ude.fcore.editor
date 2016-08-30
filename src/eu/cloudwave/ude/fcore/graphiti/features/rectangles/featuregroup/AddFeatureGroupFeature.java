package eu.cloudwave.ude.fcore.graphiti.features.rectangles.featuregroup;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddShapeFeature;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;

import eu.cloudwave.ude.fcore.graphiti.IDUtil;
import eu.cloudwave.ude.fcore.graphiti.FCORE.FeatureGroup;

public class AddFeatureGroupFeature extends AbstractAddShapeFeature {

	private static final IColorConstant FEATURE_GROUP_TEXT_FOREGROUND =
	        IColorConstant.BLACK;
	
	    private static final IColorConstant FEATURE_GROUP_FOREGROUND =
	        new ColorConstant(98, 131, 167);

	    private static final IColorConstant  FEATURE_GROUP_BACKGROUND =
	        new ColorConstant(187, 218, 247);
	 
	    public AddFeatureGroupFeature(IFeatureProvider fp) {
	        super(fp);
	    }
	 
	    public boolean canAdd(IAddContext context) {
	        // check if user wants to add a FeatureGroup
	        if (context.getNewObject() instanceof FeatureGroup) {
	            // check if user wants to add to a diagram
	            if (context.getTargetContainer() instanceof Diagram) {
	                return true;
	            }
	        }
	        return false;
	    }
	 
	    public PictogramElement add(IAddContext context) {
	    	final FeatureGroup addedFeatureGroup = (FeatureGroup) context.getNewObject();
	        final Diagram targetDiagram = (Diagram) context.getTargetContainer();
	  
	        // CONTAINER SHAPE WITH ROUNDED RECTANGLE
	        final IPeCreateService peCreateService = Graphiti.getPeCreateService();
	        final ContainerShape containerShape =
	            peCreateService.createContainerShape(targetDiagram, true);
	        IDUtil.setFeatureGroupShape(containerShape);
	  
	        // check whether the context has a size (e.g. from a create feature)
	        // otherwise define a default size for the shape
	        final int width = context.getWidth() <= 0 ? 100 : context.getWidth();
	        final int height = context.getHeight() <= 0 ? 25 : context.getHeight();
	        IGaService gaService = Graphiti.getGaService();
	        Rectangle rectangle; // need to access it later
	 
	        {
	            // create and set graphics algorithm
	            rectangle =
	                gaService.createPlainRectangle(containerShape);
	            rectangle.setForeground(manageColor(FEATURE_GROUP_FOREGROUND));
	            rectangle.setBackground(manageColor(FEATURE_GROUP_BACKGROUND));
	            rectangle.setLineWidth(2);
	            gaService.setLocationAndSize(rectangle,
	                context.getX(), context.getY(), width, height);
	 
	            // if added FeatureGroup has no resource we add it to the resource 
	            // of the diagram
	            // in a real scenario the business model would have its own resource
	            if (addedFeatureGroup.eResource() == null) {
	                     getDiagram().eResource().getContents().add(addedFeatureGroup);
	            }
	            // create link and wire it
	            link(containerShape, addedFeatureGroup);
	        }
	        
	        // SHAPE WITH TEXT
	        {
	            // create shape for text
	            Shape shape = peCreateService.createShape(containerShape, false);
	 
	            // create and set text graphics algorithm
	            String cardinality = "<" + addedFeatureGroup.getMin() + ".." + addedFeatureGroup.getMax() + ">";
	            Text text = gaService.createText(shape, cardinality);
	            text.setForeground(manageColor(FEATURE_GROUP_TEXT_FOREGROUND));
	            text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER ); 
	            // vertical alignment has as default value "center"
	            text.setFont(gaService.manageDefaultFont(getDiagram(), false, true));
	            gaService.setLocationAndSize(text, 0, 0, width, 20);
	 
	            // create link and wire it
	            link(shape, addedFeatureGroup);
	        }
	        
	        // add a chopbox anchor to the shape 
	        peCreateService.createChopboxAnchor(containerShape);
	        
	        // call the layout feature
	        layoutPictogramElement(containerShape);
	        
	        return containerShape;
	    }

}
