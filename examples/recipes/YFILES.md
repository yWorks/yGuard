yFiles
--------------

```
<yguard>
            <inoutpair in="${jar}" out="${obfjar}"/>
            
            <externalclasses>
                <pathelement location="annotations.jar"/>
            </externalclasses>

            <shrink logfile="${shrinklog}">
                <keep>
                    <!-- main method -->
                    <method name="void main(java.lang.String[])" class="${mainclass}" />
                    <!--<class classes="private" methods="private" fields="private">
                        <patternset>
                            <include name="com.*"/>
                        </patternset>
                    </class>-->

                    <method
                    class="complete.simpleeditor.SimpleEditorDemo"
                    name="void main(java.lang.String[])"/>
                    <method
                    class="com.yworks.yfiles.markup.primitives.StaticExtension"
                    name="void &lt;init&gt;()"/>
                    <method
                    class="com.yworks.yfiles.markup.primitives.StaticExtension"
                    name="void setMember(com.yworks.yfiles.graphml.Property)"/>
                    <method
                    class="com.yworks.yfiles.markup.common.FreeNodePortLocationModelParameterExtension"
                    name="void &lt;init&gt;()"/>
                    <method
                    class="com.yworks.yfiles.graphml.GraphMLReferenceExtension"
                    name="void &lt;init&gt;(java.lang.String)"/>
                    <class
                    name="com.yworks.yfiles.markup.primitives.FrameworkDefaultValueConverterHolder"/>
                    <method
                    class="com.yworks.yfiles.markup.primitives.FrameworkDefaultValueConverterHolder"
                    name="void &lt;init&gt;()"/>
                    <method
                    class="com.yworks.yfiles.markup.primitives.FrameworkDefaultValueSerializer"
                    name="void &lt;init&gt;()"/>
                    <class
                    name="com.yworks.yfiles.markup.common.InteriorLabelModelParameterExtension"
                    methods="public"/>
                    <method 
                    class="com.yworks.yfiles.graph.labelmodels.InteriorLabelModel"
                    name="com.yworks.yfiles.geometry.InsetsD getInsets()"/>
                    <method
                    class="com.yworks.yfiles.graph.labelmodels.InteriorLabelModel"
                    name="void setInsets(com.yworks.yfiles.geometry.InsetsD)"/>

		            <method 
                    name="void setRadius(double)" 
                    class="com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle"/>

		            <method 
                    name="void setInsets(com.yworks.yfiles.geometry.InsetsD)" 
                    class="com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle" />

                    <method 
                    name="void setInsets(com.yworks.yfiles.geometry.InsetsD)" 
                    class="com.yworks.yfiles.graph.styles.CollapsibleNodeStyleDecorator" />

                    <method 
                    name="void setButtonPlacement(com.yworks.yfiles.graph.labelmodels.ILabelModelParameter)"
                    class="com.yworks.yfiles.graph.styles.CollapsibleNodeStyleDecorator" />
                </keep>
            </shrink>

            <rename mainclass="${mainclass}" logfile="${renamelog}">
                <property name="error-checking" value="pedantic"/>
            </rename>
        </yguard>
```

This is a example config for a program depending on a large library such as [yFiles](https://www.yworks.com/products/yfiles).
