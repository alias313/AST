package tema4.b.cuaRemota_v2.util;

import java.io.Serializable;

/**
 *
 * @author juanluis
 */
public class Missatge implements Serializable{
  
  public enum Type {PUT,
                    GET,
                    RESPONSE,
                    CLOSE, 
                    };
  
  Type type;
  Object content;

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public Object getContent() {
    return content;
  }

  public void setContent(Object content) {
    this.content = content;
  }

}
