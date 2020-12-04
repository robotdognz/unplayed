class Tools {
  Editor editor;
  Game game;
  Tools(Editor editor, Game game){
    this.editor = editor;
    this.game = game;
  }
  
  //currently these methods are all split up, if it ends up being simpler to combine some of them, then do that
  
  //------------------Add---------------------
  public void addTile(){
    
  }
  
  public void addImage(){
    
  }
  
  public void addEvent(){
    
  }
  
  public void addPageArea(){
    
  }
  
  public void addPage(){
    if(editor.pageView){
      //do stuff
    }else{
      addPageArea();
    }
  }
  
  //------------------Erase---------------------
  public void eraseTile(){
    
  }
  
  public void eraseImage(){
    
  }
  
  public void eraseEvent(){
    
  }
  
  public void erasePageArea(){
    
  }
  
  public void erasePage(){
    if(editor.pageView){
      //do stuff
    }else{
      erasePageArea();
    }
  }
  
  //------------------Select---------------------
  public void selectTile(){
    
  }
  
  public void selectImage(){
    
  }
  
  public void selectEvent(){
    
  }
  
  public void selectPageArea(){
    
  }
  
  public void selectPage(){
    if(editor.pageView){
      //do stuff
    }else{
      selectPageArea();
    }
  }
  
}
