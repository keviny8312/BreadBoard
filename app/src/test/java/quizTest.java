import com.contigo2.cmsc355.breadboard.Quiz;

import org.junit.Test;
import java.util.regex.Pattern;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class quizTest {
    @Test
    public void testGetNameReturnsRightName(){

        //Test first constructor
        Quiz testQuiz = new Quiz("bigboy", "04-04-2018");
        assertThat(testQuiz.getName(), is("default_name"));

        //assertThat(testQuiz.getDueDate(), is())
    }


}
