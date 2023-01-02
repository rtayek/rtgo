    @Before public void setUp() throws Exception {
        System.out.println(Init.first+"enter setup");
        Logging.setLevels(Level.CONFIG);
        //watchdog=Watchdog.watchdog(Thread.currentThread());
        // check for duplicate code in other tests.
        goServer=GoServer.startServer(i%2==0?IO.anyPort:IO.noPort);
        assertNotNull("no go server!",goServer);
        final int port=goServer.serverSocket!=null?goServer.serverSocket.getLocalPort():IO.noPort;
        System.out.println("setup game on server");
        game=goServer.setUpGameOnServerAndWaitForAGame(port);
        assertNotNull("no game from server!",game);
        GTPBackEnd.sleep2(2); // try to find out why this is necessary.
        System.out.println("waiting: "+game.recorderFixture.backEnd.isWaitingForMove());
        assertNotNull("black board",game.recorderFixture.backEnd.model.board());
        width=game.recorderFixture.backEnd.model.board().width();
        depth=game.recorderFixture.backEnd.model.board().depth();
        System.out.println("exit setup");
    }
