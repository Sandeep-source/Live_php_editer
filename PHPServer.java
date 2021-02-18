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
	File temp;
	File temphtml;
	double zoom=1;
	Tabbber curtab;
	 Scene sc;
	TabPane tab_pane;
	
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
		mstg.setTitle("PHP Editer");
		mstg.getIcons().add(new Image("phpe2.png"));
		BorderPane root=new BorderPane();
		tab_pane=new TabPane();
		tab_pane.setSide(Side.LEFT);
	    sc=new Scene(root,800,700);
	    sc.getStylesheets().add("Editer.css");
		
		MenuBar mb=new MenuBar();
               
		//mstg.getIcons().add(new Image("note.png"));
		Menu file=new Menu("File");
              
		MenuItem opn=new MenuItem("Open");
		MenuItem save=new MenuItem("Save");
		MenuItem save_as=new MenuItem("Save as");
		MenuItem exit=new MenuItem("Exit");
		save.setAccelerator(KeyCombination.keyCombination("CTRL+S"));
		save_as.setAccelerator(KeyCombination.keyCombination("CTRL+SHIFT+S"));
		opn.setAccelerator(KeyCombination.keyCombination("CTRL+O"));
		exit.setAccelerator(KeyCombination.keyCombination("CTRL+E"));
	    Menu edit=new Menu("View");
	    MenuItem zoom_in=new MenuItem("zoom in");
	    MenuItem zoom_out=new MenuItem("zoom out");
	    zoom_in.setAccelerator(KeyCombination.keyCombination("CTRL+P"));
	    zoom_out.setAccelerator(KeyCombination.keyCombination("CTRL+M"));
	    edit.getItems().addAll(zoom_in,zoom_out);
		file. getItems().addAll(opn,save,save_as,exit);
		Menu hp=new Menu("Help");
		mb.getMenus().addAll(file,edit,hp);
		root.setTop(mb);
		root.setCenter(tab_pane);
		
		
		Tabbber one=new Tabbber("main");
		Tabbber add=new Tabbber("+");
		add.getStyleClass().add("add");
		tab_pane.getTabs().addAll(add,one);
		tab_pane.getSelectionModel().select(one);
		curtab=one;
	    
	   tab_pane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>(){
	   	public void changed(ObservableValue<? extends Tab> val,Tab old, Tab newval){
	   		if(newval.getText()=="+"){
	   			Tabbber tb=new Tabbber("untitled");
	   			curtab=tb;
	   			tb.txtCon.addEventHandler(MouseEvent.MOUSE_DRAGGED,(event)->{
          x=event.getScreenX();
          y=event.getScreenY();
	    });
            tb.txt.textProperty().addListener( new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> val,String o,String n){
              if(tb.counter%4==0){
            	if(tb.opend!=null)
            	temp=new File(tb.opend.getAbsolutePath()+".temp.php");
                else
                temp=new File(".temp.php");
            	if(!temp.exists()){
            		try{
              		temp.createNewFile();
              	}catch(Exception ex){
              	}
            	}
            	if(temp!=null){
            		temp.deleteOnExit();
	    		try(FileOutputStream tout=new FileOutputStream(temp)){
	    			for(char a: tb.txt.getText().toCharArray()){
	    				tout.write(a);
	    			}
	    		  Runtime r=Runtime.getRuntime();
                  Process p=r.exec("php "+temp.getAbsolutePath());
                  InputStream stream=p.getInputStream();
                  String html="";
                  int c;
				      do{
				         c=stream.read();
				         if(c!=-1){
				         	html+=(char)c;
				         }
				      }while(c!=-1);
						if(tb.opendhtml!=null)
			            	temphtml=new File(tb.opendhtml.getAbsolutePath()+".temp.html");
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
				    			for(char a: html.toCharArray()){
				    				htmlout.write(a);
				    			}
				    		}
	    			tb.engine.load(temphtml.toURI().toString());
	    		}
	    		}catch(Exception ex){

	    		}
	    	}
            
          }else{
          	tb.counter=(tb.counter+1)%2000;
          }
      }
         
         });
	   			
	   			tab_pane.getTabs().add(tb);

	   			tab_pane.getSelectionModel().select(tb);
	   		}
	   else{
           curtab=(Tabbber)newval;
           if(curtab.opend!=null)
           System.out.println(curtab.opend.getName());
          
	   	}
	   }
	   });
	   mstg.setScene(sc);
	    mstg.show();
	    zoom_out.setOnAction((ae)-> {
	    	zoom=zoom-0.1;
	    	curtab.txt.setStyle("-fx-font-size:"+zoom+"em");
	    	
	     });
	    zoom_in.setOnAction((ae)-> {
	    	zoom=zoom+0.1;
	    	curtab.txt.setStyle("-fx-font-size:"+zoom+"em");
	     });
	    exit.setOnAction((ae)->System.exit(0));
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
	    curtab.txtCon.addEventHandler(MouseEvent.MOUSE_DRAGGED,(event)->{
          x=event.getScreenX();
          y=event.getScreenY();
	    });
            curtab.txt.textProperty().addListener( new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> val,String o,String n){
              if(curtab.counter%4==0){
            	if(curtab.opend!=null)
            	temp=new File(curtab.opend.getAbsolutePath()+".temp.php");
                else
                temp=new File(".temp.php");
            	if(!temp.exists()){
            		try{
              		temp.createNewFile();
              	}catch(Exception ex){
              	}
            	}
            	if(temp!=null){
            		temp.deleteOnExit();
	    		try(FileOutputStream tout=new FileOutputStream(temp)){
	    			for(char a: curtab.txt.getText().toCharArray()){
	    				tout.write(a);
	    			}
	    		  Runtime r=Runtime.getRuntime();
                  Process p=r.exec("php "+temp.getAbsolutePath());
                  InputStream stream=p.getInputStream();
                  String html="";
                  int c;
				      do{
				         c=stream.read();
				         if(c!=-1){
				         	html+=(char)c;
				         }
				      }while(c!=-1);
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
				    			for(char a: html.toCharArray()){
				    				htmlout.write(a);
				    			}
				    		}
	    			curtab.engine.load(temphtml.toURI().toString());
	    		}
	    		}catch(Exception ex){

	    		}
	    	}
            }else{
	    		curtab.counter=(curtab.counter+1)%2000;
	    	}
         
                 
             }
         });
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

	private class Tabbber extends Tab{
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
		int counter=0;
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



