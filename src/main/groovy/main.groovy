import org.experimental.GroovyPlayground

/**
 * Created by mattgross on 3/24/2017.
 */
app = new GroovyPlayground()
app.writableField = "ABC"   // Note the JavaBean style syntax- no getter or setter required
app.makeVariables()