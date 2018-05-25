package git2surround;import java.io.File;import java.util.Date;import scala.collection.immutable.ListSet;object GitRepository {	def buildSurroundBaseline(L: MyLog, g2s: Git2Surround, s_gitRepos: String): errorCode = {			L.myErrPrintln(MyLog.tag(2)+" buildSurroundBaseline("+s_gitRepos+")");			val r_branch = """(\w+) \(branch\).*""".r;			var i_rc =errorCode.OK;			val b_alreadyBranched = g2s.l_lsbranch.exists((s: String) => s.indexOf(s_gitRepos+" (baseline)")>=0);			if(!b_alreadyBranched) {				i_rc = ScalaBatsh.execrcNoPwd(L,i_rc,"sscm mkbranch "+s_gitRepos+" "+g2s.sscmMainLine+" -sbaseline -c-"+g2s.s_sscmAccess, g2s.s_uid_pwd)._1;			};			i_rc	};	};class GitRepository(val g2s: Git2Surround, val s_gitRepos: String) {	val L = g2s.L;	var i_rc = errorCode.OK;	var i_backupedTag = 0;	var l_remoteBranches = List.empty[String];	var l_gitBranches = List.empty[gitBranch];	L.myPrintDln("\n\n\n\n\n\n\n\n\n"); 	L.myPrintDln("************************************************************************************************"); 	L.myErrPrintDln("</pre><h2>***"+MyLog.func(1)+"("+s_gitRepos+")****************************************</h2><pre>"); 	L.myPrintDln("************************************************************************************************"); 	val s_dir = g2s.s_workingDirectory + File.separatorChar + s_gitRepos.substring(s_gitRepos.lastIndexOf("/")+1);	i_rc = checkNoCheckedOut();	// clean directory	i_rc = ScalaBatsh.cleanDirectory(L, g2s.s_workingDirectory,s_gitRepos.substring(s_gitRepos.lastIndexOf("/")+1));	// clone repository	i_rc = ScalaBatsh.execrcdir(L, i_rc, "git clone "+g2s.s_gitServer+":"+s_gitRepos+"\nls -altr",g2s.s_workingDirectory)._1;	// create surround directory	i_rc = ScalaBatsh.createDirectory(L,g2s.s_workingDirectory,s_gitRepos.substring(s_gitRepos.lastIndexOf("/")+1) + "_surround");	// list remote branches	val gitremoteBranches = ScalaBatsh.execrcdir(L,i_rc,"git branch -r",s_dir);	i_rc = gitremoteBranches._1;	if(i_rc == errorCode.OK) {	  val l_rem = gitremoteBranches._2.filter(_.indexOf("  origin")==0);	  if(!l_rem.isEmpty) {		l_remoteBranches = l_rem.tail.map{_.substring(9)};		L.myErrPrintln(MyLog.tag(1)+l_remoteBranches.mkString("</pre>remote branches:<ul>\n  <li>","</li>\n  <li>","</li></ul><pre>"));		val gitbranch = ScalaBatsh.execrcdir(L, i_rc, "git branch",s_dir);		i_rc = gitbranch._1;		L.myErrPrintln(MyLog.tag(1)+gitbranch._2.mkString("</pre>current branch:<ul>\n  <li>","</li>\n  <li>","</li></ul><pre>"));	  } else {	    i_rc = errorCode.noBranches;	  };	};	l_gitBranches = l_remoteBranches.map{new gitBranch(this, _)};	L.myErrPrintln(MyLog.tag(1)+l_gitBranches.mkString("</pre>"+s_gitRepos+" branches:<ul>\n  <li>","</li>\n  <li>","</li></ul><pre>"));	i_rc = checkNoCheckedOut();	L.myErrPrintln("  "+MyLog.tag(1)+" "+s_gitRepos+", rc: {"+errorCode.nameOf(i_rc)+"}");//	throw new Exception("Fin prematuree!");	override def toString(): String = {			l_gitBranches.mkString("<b>"+s_gitRepos+"</b> branches:<ul>\n  <li>","</li>\n  <li>","</li></ul>");	};	def checkNoCheckedOut(): errorCode = {	  L.myPrintDln("***"+MyLog.func(1)+" "+MyLog.tag(2)+" check for checkout files meaning we have an issue");			val l_ls = ScalaBatsh.execrcNoPwd(L,i_rc,"sscm ls / -b"+s_gitRepos+" -p"+g2s.sscmMainLine+" -r "+g2s.s_sscmAccess, g2s.s_uid_pwd);			i_rc = l_ls._1;			if(l_ls._2.exists((s: String) => s.indexOf("-checked out")>=0)) {			  i_rc = errorCode.checkedOutFiles;//				MyLog.checkException(L,errorCode.checkedOutFiles,"2 check for checkout files meaning we have an issue for ["+s_gitRepos+"");			};			i_rc	};};