package voting;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.MockPlugin;
import be.seeseemelk.mockbukkit.ServerMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import uk.co.jcox.votemod.Main;
import uk.co.jcox.votemod.votes.BaseVote;
import uk.co.jcox.votemod.votes.VoteBan;

import static org.junit.jupiter.api.Assertions.*;


public class BaseVoteTest {
    private ServerMock server;
    private Main plugin;

    @Before
    public void setUp() {
        this.server = MockBukkit.mock();
        MockPlugin mockPlugin = MockBukkit.createMockPlugin("Vault");
        plugin = MockBukkit.load(Main.class);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testRequired() throws Exception{
       //todo get plugin config and run tests
    }
}
