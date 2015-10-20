package fi.hut.cs.drumbeat.ifc.convert.ifc2ld.web;

import java.io.File;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;


@DesignRoot
@Title("IFC2LD Converter")
@Theme("mytheme")
@Widgetset("fi.hut.cs.drumbeat.ifc.convert.ifc2ld.web.MyAppWidgetset")
@SuppressWarnings("serial")
public class MainUI extends UI {
	
	public final static float DEFAULT_WIDTH = 400.f;	
	
	public MainUI() {
		
		try {
			IfcApplication.init();
		} catch (Exception e) {
			e.printStackTrace();
			VerticalLayout mainLayout = new VerticalLayout();
			setContent(mainLayout);
			mainLayout.addComponent(
				new Label(e.toString())
			);
			return;
		}
		
		initUI();
		
	}
	
	private void initUI() {
		
		VerticalLayout mainLayout = new VerticalLayout();
		setContent(mainLayout);
		
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth("1000px");
		layout.setMargin(true);
		mainLayout.addComponent(layout);
		mainLayout.setComponentAlignment(layout, Alignment.TOP_CENTER);
		
		
		//
		// add components
		//
		layout.addComponents(
		
			new Image(
				null,
				new FileResource(new File(IfcApplication.WEB_INF_PATH + "/images/drumbeat_banner.jpg"))
			),
			
			new Label(
				"<center><h1>IFC2LD Converter</h1></center>",
				ContentMode.HTML
			)
			
		);
		
		
		TabSheet tabSheet = new TabSheet();
		layout.addComponent(tabSheet);
		
		tabSheet.addTab(new IfcConfigView(), "Config");

		tabSheet.addTab(new IfcSchemaView(), "Convert Schema");
		
		tabSheet.addTab(new IfcModelView(), "Convert Data");
		
		tabSheet.setSelectedTab(1);
	}
	


	@Override
	protected void init(VaadinRequest request) {
	}
	
	
//	@WebServlet(urlPatterns = "/*", name =  "MyUIServlet", asyncSupported = true)
//	@VaadinServletConfiguration(ui = MainUI.class, productionMode = false)
//	public static class MainUIServlet extends VaadinServlet {
//	}
	

}
