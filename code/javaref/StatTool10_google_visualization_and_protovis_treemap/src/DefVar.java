public interface DefVar {
	final static int INVALID                 = 9999;
	final static int STATUS_OPEN_P0          = 0;
	final static int STATUS_OPEN_P1          = 1;
	final static int STATUS_OPEN_P2          = 2;
	final static int STATUS_OPEN_P3          = 3;
	final static int STATUS_RELEASED         = 4;
	final static int STATUS_DEFERRED         = 5;
	final static int STATUS_CLOSED           = 6;
	final static int STATUS_DONTCARE         = 7;
	final static int STATUS_MAX              = 8;
	final static int REPORT_STACKED_CURVE    = 501;
	final static int REPORT_RADAR            = 502;
	final static double NO_HUE            	 = 1.0;
	final static int ALREADY_CLOSED_BUGS     = 0;
	final static int ALREADY_CLOSED_MIN      = 1;
	final static int ALREADY_CLOSED_MEAN     = 2;
	final static int ALREADY_CLOSED_MAX      = 3;
	final static int NOTYET_CLOSED_BUGS      = 4;
	final static int NOTYET_CLOSED_MIN       = 5;
	final static int NOTYET_CLOSED_MEAN      = 6;
	final static int NOTYET_CLOSED_MAX       = 7;

}

