package com.rohidekar.callgraph.common;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;

/**
 * TODO: Insert description here. (generated by ssarnobat)
 */
class DeferredRelationshipsV2 {

  static void handleDeferredRelationships(RelationshipsV2 relationships) {
    for (DeferredParentContainment aDeferredParentContainment :
        relationships.getDeferredParentContainments()) {
      JavaClass parentClass =
          relationships.getClassDef(aDeferredParentContainment.getParentClassName());
      handleDeferredParentContainment(relationships, aDeferredParentContainment, parentClass);
    }
    for (DeferredChildContainment containment : relationships.getDeferredChildContainment()) {
      MyClassVisitorV2.addContainmentRelationship(
          containment.getParentClass(), containment.getClassQualifiedName(), relationships, false);
    }
    for (DeferredSuperMethod deferredSuperMethod :
        relationships.getDeferSuperMethodRelationships()) {
      handleDeferredSuperMethod(relationships, deferredSuperMethod);
    }
  }

  private static void handleDeferredSuperMethod(
      RelationshipsV2 relationships, DeferredSuperMethod deferredSuperMethod) {
    MyInstruction parentInstruction = MyMethodVisitorV2.getInstruction(
        deferredSuperMethod.getparentClassOrInterface(),
        deferredSuperMethod.getunqualifiedMethodName(), relationships);
    if (parentInstruction == null) {
      System.err.println("SRIDHAR DeferredRelationships.handleDeferredSuperMethod() - Parent instruction was not found - " + deferredSuperMethod.getparentClassOrInterface().getClassName() + "::" + deferredSuperMethod.getunqualifiedMethodName());
      //System.exit(-1);
    } else {
      System.err.println("SRIDHAR DeferredRelationships.handleDeferredSuperMethod() - " + parentInstruction.getMethodNameQualified() + " -> "
            + deferredSuperMethod.gettarget().getMethodNameQualified());
      if (!relationships.methodCallExists(deferredSuperMethod.gettarget().getMethodNameQualified(),
          parentInstruction.getMethodNameQualified())) {
        relationships.addMethodCall(parentInstruction.getMethodNameQualified(),
            deferredSuperMethod.gettarget(),
            deferredSuperMethod.gettarget().getMethodNameQualified());
      }
    }
  }

  private static void handleDeferredParentContainment(RelationshipsV2 relationships,
      DeferredParentContainment aDeferredParentContainment, JavaClass parentClass) {
    if (parentClass == null) {
      try {
        parentClass = Repository.lookupClass(aDeferredParentContainment.getParentClassName());
      } catch (ClassNotFoundException e) {
        if (!Ignorer.shouldIgnore(aDeferredParentContainment.getParentClassName())) {
          System.err.println("SRIDHAR DeferredRelationships.handleDeferredParentContainment() - Found "+aDeferredParentContainment.getParentClassName());
        }
      }
    }
    if (parentClass != null) {
    	//System.out.println("SRIDHAR DeferredRelationships.handleDeferredParentContainment() - finally able to get parent containment relationship ");
      MyClassVisitorV2.addContainmentRelationship(parentClass,
          aDeferredParentContainment.getChildClass().getClassName(), relationships, false);
    } else {
    		System.err.println("SRIDHAR DeferredRelationships.handleDeferredParentContainment() - still not able to get any info about parent class " + aDeferredParentContainment.getParentClassName());
    }
  }
}
