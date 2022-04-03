import javafx.application.*;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.beans.binding.*;
import javafx.stage.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.io.*;
import java.io.File;
import javafx.scene.web.*;
import javafx.scene.image.*;
import javafx.beans.value.*;
import java.util.*;
import javafx.event.*;
import javafx.geometry.Side;
  
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.collections.*;


public class PHPServer extends Application{

    //File to store php code temporarely
	File temp;

	//file to store html code temporarely
	File temphtml;

	//initial zoom levels
	double zoom=1;

	//Current active tab
	Tabbber curtab;

	//Root scene instance
	Scene sc;

	//Tabpane to contain all tabs
	TabPane tab_pane;
	
	//TODO
	String query;
	double x;
	double y;
	
	Thread check=null;
	Stage suggest=null;
	 Scene scene;
	public static void main(String[] args){
		launch(args);

	}
	public void start(Stage mstg){
		//set title
		mstg.setTitle("PHP Editer");

		//set icon 
		mstg.getIcons().add(new Image("phpe2.png"));

		//Create root layout pane
		BorderPane root=new BorderPane();

		//initialize tabpane
		tab_pane=new TabPane();
        
        //Set alignment
		tab_pane.setSide(Side.LEFT);

		//initialize root Scene
	    sc=new Scene(root,800,700);

	    //add Style sheet
	    sc.getStylesheets().add("Editer.css");
		
		//Menubar for actions can be taken on tab
		MenuBar mb=new MenuBar();
               
		//mstg.getIcons().add(new Image("note.png"));
		//File menu for primary action 
		Menu file=new Menu("File");


        //Menu item for file menu      
		MenuItem opn=new MenuItem("Open");
		MenuItem save=new MenuItem("Save");
		MenuItem save_as=new MenuItem("Save as");
		MenuItem exit=new MenuItem("Exit");

		//Sortcuut keys for file menu 
		save.setAccelerator(KeyCombination.keyCombination("CTRL+S"));
		save_as.setAccelerator(KeyCombination.keyCombination("CTRL+SHIFT+S"));
		opn.setAccelerator(KeyCombination.keyCombination("CTRL+O"));
		exit.setAccelerator(KeyCombination.keyCombination("CTRL+E"));

		//View menu for view related customization
	    Menu edit=new Menu("View");

	    //Menu item for view Menu
	    MenuItem zoom_in=new MenuItem("zoom in");
	    MenuItem zoom_out=new MenuItem("zoom out");

	    //Shortcut keys for view menu item
	    zoom_in.setAccelerator(KeyCombination.keyCombination("CTRL+P"));
	    zoom_out.setAccelerator(KeyCombination.keyCombination("CTRL+M"));

	    //Add view menu items to View Menu  
	    edit.getItems().addAll(zoom_in,zoom_out);

	    //Add file menu items
		file. getItems().addAll(opn,save,save_as,exit);

		//Create help menu
		Menu hp=new Menu("Help");

		//add all menus to menubar
		mb.getMenus().addAll(file,edit,hp);

		//Add menubar to the of root layout
		root.setTop(mb);

		//Add tabpane to the center of root layout 
		root.setCenter(tab_pane);
		
		//Create initial tab object of custom tab class (Tabbbler)
		Tabbber one=new Tabbber("main");

		//Tab to create new tabs 😅
		Tabbber add=new Tabbber("+");
		add.TYPE=Tabbber.TAB_OPEN_NEW;
		add.getStyleClass().add("add");
		tab_pane.getTabs().addAll(add,one);
		tab_pane.getSelectionModel().select(one);
		curtab=one;


		//Change listener-> listen for changes in text inside text area and make preview of text available in web view

		ChangeListener changeListener= new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> val,String o,String n){

            //New Thread to reduce work load on main thread
            new Thread(()->{

            	//if in the current tab a file is opened then open temporary file for php code with the name of file in same location
            	if(curtab.opend!=null)
            	 temp=new File(curtab.opend.getAbsolutePath()+".temp.php");

                //if currently no file is opened in current tab then open file without name as .temp.php
                else
                temp=new File(".temp.php");

                //if opened file does not exits create new file 
            	if(!temp.exists()){
            		try{
              		temp.createNewFile();
              	}catch(Exception ex){
              	}
            	}
            	//if file created successfully
            	if(temp!=null){
                //Delete temporary file after window closed
            	temp.deleteOnExit();

            	//Create output stream to write php code written in text Area to temporary file
	    		try(FileOutputStream tout=new FileOutputStream(temp)){
	    			// Write code file 
	    			for(char a: curtab.txt.getText().toCharArray()){
	    				tout.write(a);
	    			}

	    		  //get Runtime and call php program 
	    		  Runtime r=Runtime.getRuntime();
                  Process p=r.exec("php "+temp.getAbsolutePath());

                  //Get output from php program
                  InputStream stream=p.getInputStream();
                  String html="";
                  int c;
				      do{
				         c=stream.read();
				         if(c!=-1){
				         	html+=(char)c;
				         }
				      }while(c!=-1);
				        //if there is opened file for html open file with that file name
						if(curtab.opendhtml!=null)

			            	temphtml=new File(curtab.opendhtml.getAbsolutePath()+".temp.html");
			            //otherwise open temp file without name as temp
			                else
			                temphtml=new File(".temp.html");
			            //Open file does not exists create new
			            if(!temphtml.exists()){
			            		try{
			              		temphtml.createNewFile();
			              	}catch(Exception ex){
			              	}
			            	}

			            //Temp html file created successfully  
			            if(temphtml!=null){
			            	//Delete when window is closed
			            	temphtml.deleteOnExit();
			            	//write output of php program to html file 
				    		try(PrintWriter htmlout=new PrintWriter(new FileOutputStream(temphtml))){
				    			String[] str=html.split("\n");
				    			for(int i=0;i<str.length;i++){
				    				htmlout.println(str[i]);
				    			}
				    		}

				    // load temp html file to webview in current tab
				    Platform.runLater(()->{
				    	curtab.engine.load(temphtml.toURI().toString());
				    });
	    			
	    		}
	    		}catch(Exception ex){

	    		}
	    	}

            }).start();
          
          }
 
         };
	    

	   //Obeserve tab selection 
	   tab_pane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>(){
	   	public void changed(ObservableValue<? extends Tab> val,Tab old, Tab newval){
	   		Tabbber tmpTab=(Tabbber)newval;
	   		//Tab clicked to open new tab
	   		if(tmpTab.TYPE==Tabbber.TYPE_OPEN_NEW){

	   			//add title to new tab
	   			curtab=new Tabbber("untitled");

	   			//Add mouse listener useless till now 
	   			curtab.txtCon.addEventHandler(MouseEvent.MOUSE_DRAGGED,(event)->{
                     x=event.getScreenX();
                     y=event.getScreenY();
	            });

	           //Add change listener to observe changes in Text Area
               curtab.txt.textProperty().addListener(changeListener);
               //Add tab to Tab pane 
	   		   tab_pane.getTabs().add(curtab);
	   		   //Select current tab
               tab_pane.getSelectionModel().select(curtab);
	   		}
	   	    //If One of previous tabs selected
	         else{
                //make selected tab current tab;
                curtab=(Tabbber)newval;
                if(curtab.opend!=null)
                      System.out.println(curtab.opend.getName());
          
	   	}
	   }
	   });

	   //Add Scene to the stage
	   mstg.setScene(sc);

	   //Show Gui to user
	   mstg.show();

	   //Add zoom listener
	   zoom_out.setOnAction((ae)-> {
	    	zoom=zoom-0.1;
	    	curtab.txt.setStyle("-fx-font-size:"+zoom+"em");
	    	
	    });
	    zoom_in.setOnAction((ae)-> {
	    	zoom=zoom+0.1;
	    	curtab.txt.setStyle("-fx-font-size:"+zoom+"em");
	     });
	    //Exit window when user choose exit menu item
	    exit.setOnAction((ae)->System.exit(0));

	    //save opened file 
	    save.setOnAction((ae)->{
	    	if(curtab.opend!=null){
	    		try(FileOutputStream fout=new FileOutputStream(curtab.opend)){
	    			for(char a: curtab.txt.getText().toCharArray()){
	    				fout.write(a);
	    			}
	    		}catch(Exception ex){

	    		}
	    	}else{
	    		FileChooser flc=new FileChooser();
	    	flc.setTitle("save file");
	    	File fl=flc.showSaveDialog(mstg);
	    	if(fl!=null){
              try{
              	FileOutputStream fout=new FileOutputStream(fl);
              	for(char a : curtab.txt.getText().toCharArray()){
              		fout.write(a);
              	} 
              }catch(Exception ex){
              	System.out.println("bekar hay tu");
              }

	    	}
	    	}
	    });

	    //Mouse drag event useless till  now -> tring to get mouse location to show context menu 
	    curtab.txtCon.addEventHandler(MouseEvent.MOUSE_DRAGGED,(event)->{
          x=event.getScreenX();
          y=event.getScreenY();
	    });

	    //Change listener to  observe text area
        curtab.txt.textProperty().addListener(changeListener);

        //Save as option to save file in defferent file formats 
	    save_as.setOnAction((ae)->{
	    	FileChooser flc=new FileChooser();
	    	flc.setTitle("save file");
	    	File fl=flc.showSaveDialog(mstg);
	    	if(fl!=null){
              try{
              	FileOutputStream fout=new FileOutputStream(fl);
              	for(char a : curtab.txt.getText().toCharArray()){
              		fout.write(a);
              	} 
              }catch(Exception ex){
              	System.out.println("bekar hay tu");
              }

	    	}
	    });

	    //open file menu to open files
	    opn.setOnAction((ae)->{
	    	FileChooser flc=new FileChooser();
	    	flc.setTitle("open file");
	    	curtab.opend=flc.showOpenDialog(mstg);
	    	String str="";
	    	if(curtab.opend!=null)
	    	{
	    		try{
	    		FileInputStream fin=new FileInputStream(curtab.opend);
	    		
	    		int size=fin.available();
	    		for(int i=0;i<size;i++){

	    			str+=(char)fin.read();
	    		}
	    	    curtab.setText(curtab.opend.getName());
	    	    
	    		curtab.txt.setText(str);
	    	}catch(Exception ek){
	    			System.out.print("lag gaye");
	    		}
	    		 System.out.println(curtab.opend.getName());
	    	//Save temp file with new file and show in web view
             if(curtab.opendhtml!=null)
			            	temphtml=new File(curtab.opendhtml.getAbsolutePath()+".temp.html");
			                else
			                temphtml=new File(".temp.html");
			            	if(!temphtml.exists()){
			            		try{
			              		temphtml.createNewFile();
			              	}catch(Exception ex){
			              	}
			            	}
			            	if(temphtml!=null){
			            		temphtml.deleteOnExit();
				    		try(FileOutputStream htmlout=new FileOutputStream(temphtml)){
				    			for(char a: str.toCharArray()){
				    				htmlout.write(a);
				    			}
				    		}catch(Exception ex){
				    			System.out.println("bas bahut hua");
				    		}
	    			curtab.engine.load(temphtml.toURI().toString());
	    		}
              
              System.out.println("WEb");
              
	    	}
	    });

}

	


	public void stop(){
	
	}

   //Custom tab class 
	private class Tabbber extends Tab{
		final int  TAB_REGULAR=2;
		int TYPE=TAB_REGULAR;
		final int TAB_OPEN_NEW=1;
		
		HBox topcon;
		VBox webcon;
		VBox txtCon;
		WebView web;
		WebEngine engine;
		TextArea txt;
		File opend=null;
		File opendhtml=null;
	    double x;
		double y;

		public Tabbber(String str){
        this.setText(str);
        topcon=new HBox();
        txtCon=new VBox();
        webcon=new VBox();
        web=new WebView();
        engine=web.getEngine();
        
        engine.setOnAlert((event)->{
            Alert al=new Alert(Alert.AlertType.WARNING,event.getData());
            al.showAndWait();
        });
         engine.setConfirmHandler((event)->{
            Alert al=new Alert(Alert.AlertType.CONFIRMATION,event,ButtonType.YES,ButtonType.NO,ButtonType.CANCEL);
           
        	Optional<ButtonType> res=al.showAndWait();
        	if(res.get()==ButtonType.YES)
        	{
        		return true;
        	}else{
        		return false;
        	}
        });
         engine.setPromptHandler((event)->{
         	TextInputDialog intxt=new TextInputDialog(event.getDefaultValue());
         	intxt.setContentText(event.getMessage());
         	Optional<String> res=intxt.showAndWait();
         	return res.get();

         });
       
        txt=new TextArea();
        web.setPrefWidth(sc.getWidth()/2);
        web.prefWidthProperty().bind(sc.widthProperty());
        web.prefHeightProperty().bind(sc.heightProperty());
        txt.setPrefWidth(sc.getWidth()/2);
        txt.prefWidthProperty().bind(sc.widthProperty());
        txt.prefHeightProperty().bind(sc.heightProperty());
        Button span=new Button("|");
        span.setCursor(Cursor.H_RESIZE);
        webcon.getChildren().addAll(web);
       
        txtCon.getChildren().add(txt);
        webcon.setVgrow(web,Priority.ALWAYS);
        topcon.getChildren().addAll(webcon,span,txt);
        if(span!=null) {
        span.addEventHandler(MouseEvent.MOUSE_PRESSED,new EventHandler<MouseEvent>(){
        	public void handle(MouseEvent ae){
            x=ae.getScreenX();
            y=ae.getScreenY();
        }
        });
        span.addEventHandler(MouseEvent.MOUSE_DRAGGED,new EventHandler<MouseEvent>(){
        	public void handle(MouseEvent ae){
            web.prefWidthProperty().bind(sc.widthProperty().subtract(x-ae.getScreenX()));
            txt.prefWidthProperty().bind(sc.widthProperty().add(x-ae.getScreenX()));

          }
           
        });
    }
        topcon.setHgrow(txtCon,Priority.ALWAYS);
        span.prefHeightProperty().bind(sc.heightProperty());
        topcon.setHgrow(webcon,Priority.ALWAYS);
        this.setContent(topcon);
		}
	}


}	



