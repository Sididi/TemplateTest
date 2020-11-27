import com.redpills.correction.framework.domain.OutputCorrectionTask
import org.junit.Test

class OutputTest {

    @Test
    fun outputTest() {
        val expectedMark = 20

        val task = OutputCorrectionTask().apply {
            script = "echo \"hello world\""
            expectedResult = "hello world"
            hint = "echo test"
            mark = expectedMark
            stream = 1
        }

        val results = task.execute()

        results.forEach { assert(it.mark != expectedMark) }
    }
}