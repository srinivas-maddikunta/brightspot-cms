package com.psddev.cms.db;

import com.psddev.dari.db.Database;
import com.psddev.dari.db.ObjectField;
import com.psddev.dari.db.ObjectType;
import com.psddev.dari.db.Record;
import com.psddev.dari.db.Query;
import com.psddev.dari.db.ReferentialText;
import com.psddev.dari.db.State;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Production Guide class to hold information about Content types and their associated fields
 * 
 * ObjectType and ObjectField don't currently lend themselves well to Modification classes or else this 
 * (and the other Guide<xxx> classes) would have been implemented as a modification class
 */
@Record.LabelFields({ "documentedType/name" })
public class GuideType extends Record {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GuideType.class);

	@ToolUi.Note("Content type for Production Guide information")
	@Required
	@Indexed
	ObjectType documentedType;

	@ToolUi.Note("Production Guide information about this content type")
	@ToolUi.Hidden
	// No plan for this yet - may not be needed
	ReferentialText description;

	@ToolUi.Note("Production Guide field descriptions for this content type (Note that any fields within an embedded type should be defined separately in a Guide for the embedded type)")
	private List<GuideField> fieldDescriptions;

	public ObjectType getDocumentedType() {
		return documentedType;
	}

	public void setDocumentedType(ObjectType documentedType) {
		this.documentedType = documentedType;
	}

	public ReferentialText getDescription() {
		return description;
	}

	public void setDescription(ReferentialText description) {
		this.description = description;
	}

	public List<GuideField> getFieldDescriptions() {
		return fieldDescriptions;
	}

	/*
	 * Add a field description entry. Assumes that entry doesn't already exist.
	 */
	public void addFieldDescription(GuideField fieldDescription) {
		if (fieldDescriptions == null) {
			fieldDescriptions = new ArrayList<GuideField>();
		}
		fieldDescriptions.add(fieldDescription);
	}

	public void setFieldDescriptions(List<GuideField> fieldDescriptions) {
		this.fieldDescriptions = fieldDescriptions;
	}

	public ReferentialText getFieldDescription(String fieldName, String fieldDisplayName,
			boolean createIfMissing) {
		ReferentialText desc = null;
		if (fieldDescriptions != null) {
			for (GuideField gf : fieldDescriptions) {
				if (gf.getFieldName().equals(fieldName)) {
					// we take this opportunity to synch the display name
					if (createIfMissing && fieldDisplayName != null) {
						gf.setDisplayName(fieldDisplayName);
					}
					return gf.getDescription();
				}
			}
		}
		if (createIfMissing == true) {
			setFieldDescription(fieldName, fieldDisplayName, null, false);
		}
		return desc;
	}

	public GuideField getGuideField(String fieldName) {
		if (fieldDescriptions != null) {
			for (GuideField gf : fieldDescriptions) {
				if (gf.getFieldName().equals(fieldName)) {
					return gf;
				}
			}
		}
		return null;
	}

	public void setFieldDescription(String fieldName, String fieldDisplayName,
			ReferentialText description, boolean annotation) {
		ReferentialText desc = null;
		if (fieldDescriptions != null) {
			for (GuideField gf : fieldDescriptions) {
				if (gf.getFieldName().equals(fieldName)) {
					if (fieldDisplayName != null) {
						gf.setDisplayName(fieldDisplayName);
					}
					gf.setDescription(description);
					gf.setFromAnnotation(annotation);
					return;
				}
			}
		}
		// if didn't already exist
		GuideField gf = new GuideField();
		gf.setFieldName(fieldName);
		if (fieldDisplayName != null) {
			gf.setDisplayName(fieldDisplayName);
		}
		gf.setDescription(description);
		gf.setFromAnnotation(annotation);
		addFieldDescription(gf);

	}

	public void generateFieldDescriptionList() {
		// Create an entry for each field
		ObjectType type = getDocumentedType();
		if (type != null) {
			List<ObjectField> fields = type.getFields();
			for (ObjectField field : fields) {
				getFieldDescription(field.getInternalName(), field.getDisplayName(), true);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.psddev.dari.db.Record#beforeSave()
	 */
	public void beforeSave() {
		generateFieldDescriptionList();
	}

	@Record.Embedded
	@Record.LabelFields({ "displayName" })
	public static class GuideField extends Record {

		@Required
		@Indexed
		@ToolUi.Note("Internal fieldname in this object type. This is used to query the object and must exactly match the internal name")
		String fieldName;
		
		@ToolUi.Note("Fieldname as it's displayed in UI (if different from the internal fieldname)")
		String displayName;

		@ToolUi.Note("Production Guide information about this field")
		ReferentialText description;

		// True if the description was populated from an annotation (if false,
		// annotations are ignored)
		@ToolUi.Hidden
		boolean fromAnnotation;

		public String getFieldName() {
			return fieldName;
		}

		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}

		public String getDisplayName() {
			return displayName;
		}

		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}

		public ReferentialText getDescription() {
			return description;
		}

		public void setDescription(ReferentialText description) {
			this.description = description;
		}

		public boolean isFromAnnotation() {
			return fromAnnotation;
		}

		public void setFromAnnotation(boolean fromAnnotation) {
			this.fromAnnotation = fromAnnotation;
		}

	}

	public static final class Static {

		public static ReferentialText getFieldDescription(State state,
				String fieldName) {
			if (state != null) {
				ObjectType typeDefinition = state.getType();
				GuideType guide = getGuideType(typeDefinition);
				if (guide != null) {
					return guide.getFieldDescription(fieldName, null, false);
				}
			}
			return null;
		}

		/*
		 * Query to get a T/F as to whether this field has any information we include in 
		 * the field description (e.g. used to determine whether ? link is displayed in UI
		 */
		public static boolean hasFieldGuideInfo(State state, String fieldName) {
			ObjectField field = state.getField(fieldName);
			if (field.isRequired())
				return true;
			if (field.getMaximum() != null)
				return true;
			if (field.getMinimum() != null)
				return true;
			if (field.getDefaultValue() != null)
				return true;
			ReferentialText desc = getFieldDescription(state, fieldName);
			if (desc != null && !desc.isEmpty())
				return true;

			return false;
		}

		/*
		 * Retrieve the existing GuideType instance for a given ObjectType.
		 * If none exists, null is returned
		 */
		public static GuideType getGuideType(ObjectType objectType) {
			return Query.from(GuideType.class)
					.where("documentedType = ?", objectType.getId()).first();
		}

		/*
		 * Retrieve a GuideType instance for the parent type of a given field, creating one if it
		 * doesn't already exist.
		 */
		public static synchronized GuideType findOrCreateGuide(ObjectField field) {
			GuideType guide = Query.from(GuideType.class)
					.where("documentedType = ?", field.getParentType().getId()).first();
			if (guide == null) {
				guide = createGuide(field.getParentType());
			}
			return guide;
		}

		/*
		 * Retrieve a GuideType instance for the given ObjectType, creating one if it
		 * doesn't already exist.
		 */
		public static GuideType findOrCreateGuide(ObjectType documentedType) {
			GuideType guide = Query.from(GuideType.class)
					.where("documentedType = ?", documentedType.getId()).first();
			if (guide == null) {
				guide = createGuide(documentedType);
			}
			return guide;
		}

		/*
		 * Create a GuideType instance for the given ObjectType. To allow for thread/transaction safety, this
		 * is synchronized and double checks to ensure it hasn't already been created.
		 */
		public static synchronized GuideType createGuide(
				ObjectType documentedType) {
			GuideType guide = Query.from(GuideType.class)
					.where("documentedType = ?", documentedType.getId()).first();
			if (guide == null) {
				LOGGER.info("Creating a production guide instance for type: "
						+ documentedType);
				guide = new GuideType();
				guide.setDocumentedType(documentedType);
				guide.saveImmediately();
			}
			return guide;
		}

		public static synchronized void setDescription(ObjectField field,
				ReferentialText descText, boolean fromAnnotation) {
			GuideType guide = Static.findOrCreateGuide(field);
			guide.setFieldDescription(field.getInternalName(), field.getDisplayName(), descText, fromAnnotation);
			guide.saveImmediately();
		}

		/*
		 * Generate guide instances for any content types usable in the site's
		 * templates
		 */
		public static void createDefaultTypeGuides() {
			// List<ObjectType> types = Template.Static.findUsedTypes(null);
			List<Template> templates = Query.from(Template.class).selectAll();
			for (Template template : templates) {
				for (ObjectType t : template.getContentTypes()) {
					findOrCreateGuide(t);
				}
			}
		}
	}

}