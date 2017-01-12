package fi.aalto.cs.drumbeat.ifc.convert.ifc2ld.web;

import java.io.File;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Notification.Type;


@DesignRoot
@Title("IFC2LD Converter")
@Theme("mytheme")
@Widgetset("fi.aalto.cs.drumbeat.ifc.convert.ifc2ld.web.MyAppWidgetset")
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
				new FileResource(new File(IfcApplication.WEB_INF_DIR_PATH + "/images/drumbeat_banner.jpg"))
			),
			
			new Label(
				"<center><h1>IFC2LD Converter</h1></center>",
				ContentMode.HTML
			)
			
		);
		
		
		TabSheet tabSheet = new TabSheet();
		layout.addComponent(tabSheet);
		
		try {
		
			tabSheet.addTab(new ConfigView(), "Config");
	
			tabSheet.addTab(new ConvertSchemaView(), "Convert Schema");
			
			tabSheet.addTab(new ConvertModelView(), "Convert Data");
			
//			tabSheet.setSelectedTab(2);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			layout.addComponent(new Label(e.getMessage()));
//			Notification.show(e.getMessage() != null ? e.getMessage() : "Unexpected error", Type.ERROR_MESSAGE);
			
		}
	}
	


	@Override
	protected void init(VaadinRequest request) {
	}
	
	
//	@WebServlet(urlPatterns = "/*", name =  "MyUIServlet", asyncSupported = true)
//	@VaadinServletConfiguration(ui = MainUI.class, productionMode = false)
//	public static class MainUIServlet extends VaadinServlet {
//	}
	

}
