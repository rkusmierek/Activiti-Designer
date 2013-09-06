package org.activiti.designer.kickstart.form.diagram;

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.kickstart.form.diagram.layout.KickstartFormLayouter;
import org.activiti.designer.kickstart.form.diagram.shape.BusinessObjectShapeController;
import org.activiti.designer.kickstart.form.diagram.shape.DatePropertyShapeController;
import org.activiti.designer.kickstart.form.diagram.shape.ListPropertyShapeController;
import org.activiti.designer.kickstart.form.diagram.shape.PropertyGroupShapeController;
import org.activiti.designer.kickstart.form.diagram.shape.TextPropertyShapeController;
import org.activiti.designer.kickstart.form.features.AddFormComponentFeature;
import org.activiti.designer.kickstart.form.features.CreateDatePropertyFeature;
import org.activiti.designer.kickstart.form.features.CreateFormGroupFeature;
import org.activiti.designer.kickstart.form.features.CreateListPropertyFeature;
import org.activiti.designer.kickstart.form.features.CreateTextAreaPropertyFeature;
import org.activiti.designer.kickstart.form.features.CreateTextInputPropertyFeature;
import org.activiti.designer.kickstart.form.features.DeleteFormComponentFeature;
import org.activiti.designer.kickstart.form.features.DirectEditFormComponentFeature;
import org.activiti.designer.kickstart.form.features.FormPropertyResizeFeature;
import org.activiti.designer.kickstart.form.features.MoveFormComponentFeature;
import org.activiti.designer.kickstart.form.features.UpdateFormComponentFeature;
import org.activiti.designer.util.editor.KickstartFormIndependenceSolver;
import org.activiti.designer.util.editor.KickstartFormMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.DefaultFeatureProvider;

public class KickstartFormFeatureProvider extends DefaultFeatureProvider {

    protected KickstartFormLayouter formLayouter;
    protected List<BusinessObjectShapeController> shapeControllers;
    
	public KickstartFormFeatureProvider(IDiagramTypeProvider dtp) {
		super(dtp);
		// Use resolver based on the memory-model for the form
		setIndependenceSolver(new KickstartFormIndependenceSolver(dtp));
		this.formLayouter = new KickstartFormLayouter();
		
		this.shapeControllers = new ArrayList<BusinessObjectShapeController>();
		shapeControllers.add(new DatePropertyShapeController(this));
		shapeControllers.add(new TextPropertyShapeController(this));
		shapeControllers.add(new PropertyGroupShapeController(this));
		shapeControllers.add(new ListPropertyShapeController(this));
	}
	
	/**
	 * @param businessObject object to get a {@link BusinessObjectShapeController} for
	 * @return a {@link BusinessObjectShapeController} capable of creating/updating shapes
	 * of for the given businessObject.
	 * @throws IllegalArgumentException When no controller can be found for the given object.
	 */
	public BusinessObjectShapeController getShapeController(Object businessObject) {
	  for(BusinessObjectShapeController controller : shapeControllers) {
	    if(controller.canControlShapeFor(businessObject)) {
	      return controller;
	    }
	  }
	  throw new IllegalArgumentException("No controller can be found for object: " + businessObject);
	}
	
	/**
	 * @return true, if a {@link BusinessObjectShapeController} is available for the given business object.
	 */
	public boolean hasShapeController(Object businessObject) {
	  for(BusinessObjectShapeController controller : shapeControllers) {
        if(controller.canControlShapeFor(businessObject)) {
          return true;
        }
      }
	  return false;
	}

	@Override
	public ICreateFeature[] getCreateFeatures() {
	  return new ICreateFeature[]{ 
	      new CreateTextInputPropertyFeature(this), new CreateTextAreaPropertyFeature(this),
	      new CreateDatePropertyFeature(this), new CreateFormGroupFeature(this), new CreateListPropertyFeature(this)
	  };
	}
	
	@Override
	public IAddFeature getAddFeature(IAddContext context) {
	  return new AddFormComponentFeature(this);
	}
	
	@Override
	public IUpdateFeature getUpdateFeature(IUpdateContext context) {
	  return new UpdateFormComponentFeature(this);
	}
	
	@Override
	public IResizeShapeFeature getResizeShapeFeature(IResizeShapeContext context) {
	  return new FormPropertyResizeFeature(this);
	}
	
	@Override
	public IMoveShapeFeature getMoveShapeFeature(IMoveShapeContext context) {
	  return new MoveFormComponentFeature(this);
	}
	
	@Override
	public IDirectEditingFeature getDirectEditingFeature(IDirectEditingContext context) {
	  return new DirectEditFormComponentFeature(this);
	}
	
	@Override
	public IDeleteFeature getDeleteFeature(IDeleteContext context) {
	  return new DeleteFormComponentFeature(this);
	}
	
	@Override
	public Object getBusinessObjectForPictogramElement(PictogramElement pictogramElement) {
	  // Business-object for the diagram is ALWAYS the root form-definition
	  if(pictogramElement instanceof Diagram) {
	    KickstartFormMemoryModel model = ModelHandler.getKickstartFormMemoryModel(EcoreUtil.getURI(pictogramElement));
	    if(model != null) {
	      return model.getFormDefinition();
	    }
	  }
	  return super.getBusinessObjectForPictogramElement(pictogramElement);
	}
	
	public KickstartFormLayouter getFormLayouter() {
      return formLayouter;
    }
}
