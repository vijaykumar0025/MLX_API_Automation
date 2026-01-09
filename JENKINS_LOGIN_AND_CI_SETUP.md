# 🚀 Jenkins Login & CI Setup - Complete Step-by-Step Guide

## Part 1: How to Login to Jenkins

### Step 1: Open Jenkins in Your Browser

1. **Open your web browser** (Chrome, Edge, Firefox)
2. **Go to Jenkins URL:**
   ```
   http://localhost:8080
   ```
   
   **Alternative URLs (if localhost doesn't work):**
   - `http://127.0.0.1:8080`
   - `http://YOUR_COMPUTER_NAME:8080`
   - Check your Jenkins installation location for the actual URL

### Step 2: First-Time Setup (If This is Your First Login)

**If you see "Unlock Jenkins" page:**

1. **Find the Initial Admin Password:**
   - Jenkins displays the password file location on screen
   - **Windows:** `C:\Program Files\Jenkins\secrets\initialAdminPassword`
   - **Alternative:** `C:\Users\YOUR_USERNAME\.jenkins\secrets\initialAdminPassword`

2. **Get the Password:**
   - Open the file location in File Explorer
   - Open `initialAdminPassword` with Notepad
   - Copy the entire password (long alphanumeric string)
   - Paste it into Jenkins unlock page
   - Click **Continue**

3. **Install Plugins:**
   - Choose **Install suggested plugins** (recommended)
   - Wait 5-10 minutes for installation to complete

4. **Create First Admin User:**
   - Username: `admin` (or your preferred username)
   - Password: Choose a strong password
   - Full name: Your name
   - Email: Your email address
   - Click **Save and Continue**

5. **Jenkins URL Configuration:**
   - Keep default: `http://localhost:8080/`
   - Click **Save and Finish**
   - Click **Start using Jenkins**

### Step 3: Regular Login

**If Jenkins is already set up:**

1. Go to: `http://localhost:8080`
2. Enter your **Username**
3. Enter your **Password**
4. Click **Sign in** or **Log in**

**✅ You should now see the Jenkins Dashboard!**

---

## Part 2: Creating Your CI Pipeline (MLX API Automation)

### Prerequisites Checklist
Before creating the pipeline, ensure:
- [ ] Jenkins is running and you're logged in
- [ ] You have the GitHub repository URL: `https://github.com/vijaykumar0025/MLX_API_Automation.git`
- [ ] You have your GitHub Personal Access Token ready
- [ ] Required plugins are installed

---

## 🔧 PHASE 1: Install Required Plugins (10 minutes)

### Step 1: Open Plugin Manager
1. From Jenkins Dashboard, click **Manage Jenkins** (left sidebar)
2. Click **Manage Plugins** (or **Plugins** in newer versions)

### Step 2: Go to Available Plugins
1. Click **Available plugins** tab
2. Use the search box to find plugins

### Step 3: Install These Plugins One by One

**Search for and install:**

1. **Git Plugin**
   - Search: `Git Plugin`
   - Check the checkbox
   
2. **GitHub Plugin**
   - Search: `GitHub Plugin`
   - Check the checkbox
   
3. **Maven Integration Plugin**
   - Search: `Maven Integration`
   - Check the checkbox
   
4. **Pipeline Plugin**
   - Search: `Pipeline`
   - Check the checkbox
   
5. **HTML Publisher Plugin**
   - Search: `HTML Publisher`
   - Check the checkbox
   
6. **Email Extension Plugin**
   - Search: `Email Extension`
   - Check the checkbox
   
7. **JUnit Plugin**
   - Search: `JUnit`
   - Check the checkbox

### Step 4: Install Plugins
1. After checking all 7 plugins, scroll to bottom
2. Click **Download now and install after restart**
3. ✅ Check **Restart Jenkins when installation is complete and no jobs are running**
4. Wait for Jenkins to restart (2-3 minutes)
5. Log back in when prompted

---

## 🛠️ PHASE 2: Configure Tools (5 minutes)

### Step 1: Go to Global Tool Configuration
1. Click **Manage Jenkins** (left sidebar)
2. Click **Global Tool Configuration**

### Step 2: Configure Maven

1. **Scroll down to "Maven" section**
2. Click **Add Maven** button
3. Fill in:
   - **Name:** `Maven-3.9.11` (exactly as shown)
   - ✅ Check **Install automatically**
   - **Version:** Select `3.9.11` from dropdown
4. Click **Apply** (at bottom)

### Step 3: Configure JDK

1. **Scroll down to "JDK" section**
2. Click **Add JDK** button
3. Fill in:
   - **Name:** `JDK-11` (exactly as shown)
   - ✅ Check **Install automatically**
   - **Install from:** Select `java.net`
   - **Version:** Select `jdk-11.0.x` (latest 11 version)
4. Click **Save** (at bottom)

**✅ Tools are now configured!**

---

## 🔑 PHASE 3: Create GitHub Personal Access Token (3 minutes)

### Step 1: Go to GitHub Token Page
1. Open new browser tab
2. Go to: https://github.com/settings/tokens
3. Or navigate: GitHub → Settings → Developer settings → Personal access tokens → Tokens (classic)

### Step 2: Generate New Token
1. Click **Generate new token (classic)**
2. GitHub may ask for password - enter it

### Step 3: Configure Token
Fill in these details:

- **Note:** `Jenkins MLX API Automation`
- **Expiration:** `90 days` (or your preference)
- **Select scopes:**
  - ✅ `repo` - Check the main checkbox (all sub-items will auto-check)
    - This includes: repo:status, repo_deployment, public_repo, repo:invite, security_events
  - ✅ `admin:repo_hook` - Check this main checkbox
    - This includes: write:repo_hook, read:repo_hook

### Step 4: Generate and Copy Token
1. Scroll to bottom
2. Click **Generate token** (green button)
3. **⚠️ IMPORTANT:** Copy the token immediately!
4. **Save it** in a text file temporarily (you won't see it again!)

Example token format: `ghp_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx`

**✅ Token created! Keep it safe for next step.**

---

## 🚀 PHASE 4: Create Jenkins Pipeline Job (10 minutes)

### Step 1: Create New Item
1. Go back to Jenkins Dashboard
2. Click **New Item** (left sidebar)

### Step 2: Configure Job Name and Type
1. **Enter an item name:** `MLX_API_Automation_CI_ONLY`
2. **Select:** Click on **Pipeline** (scroll down if needed)
3. Click **OK** at bottom

### Step 3: General Settings

**Description:**
```
MLX API Automation - CI ONLY (No Deployment)
✓ Runs automated tests against staging/dev API
✓ Generates beautiful reports with highlighted order numbers
✓ Sends email notifications
✗ Does NOT deploy anything
```

**Discard Old Builds:**
1. ✅ Check **Discard old builds**
2. **Strategy:** Log Rotation
3. **Days to keep builds:** `30`
4. **Max # of builds to keep:** `10`

### Step 4: Build Triggers

Choose one or more:

**Option 1: Scheduled Daily Run**
1. ✅ Check **Build periodically**
2. **Schedule:** Enter `H 2 * * *` (runs daily at 2 AM)

**Option 2: Check GitHub Every 15 Minutes**
1. ✅ Check **Poll SCM**
2. **Schedule:** Enter `H/15 * * * *`

**Option 3: Manual Only**
- Leave all unchecked (you'll run manually)

### Step 5: Pipeline Configuration

This is the MOST IMPORTANT part!

1. **Definition:** Select `Pipeline script from SCM`

2. **SCM:** Select `Git`

3. **Repository URL:** 
   ```
   https://github.com/vijaykumar0025/MLX_API_Automation.git
   ```

4. **Credentials:** Click **Add** → **Jenkins**

   **Add Credentials Dialog:**
   - **Domain:** Global credentials
   - **Kind:** Username with password
   - **Scope:** Global
   - **Username:** `vijaykumar0025`
   - **Password:** [Paste your GitHub token from Phase 3]
   - **ID:** `github-mlx-automation-token`
   - **Description:** `GitHub Personal Access Token - MLX Automation`
   - Click **Add**

5. **Credentials:** Now select the credential you just created from dropdown
   - Should show: `vijaykumar0025/****** (GitHub Personal Access Token - MLX Automation)`

6. **Branches to build:**
   - **Branch Specifier:** `*/main`
   - (Change to `*/master` if your repo uses master branch)

7. **Repository browser:** (Auto) - leave as is

8. **Script Path:** `Jenkinsfile.CI-ONLY`

### Step 6: Save the Job
1. Scroll to bottom
2. Click **Save**

**✅ Pipeline job created!**

---

## 🎯 PHASE 5: Run Your First Build (2 minutes)

### Step 1: Go to Job Page
- You should automatically be on the job page
- If not, click on `MLX_API_Automation_CI_ONLY` from dashboard

### Step 2: Start Build
1. Click **Build with Parameters** (left sidebar)
   - If you don't see this, click **Build Now** instead

2. **If you see parameters, configure them:**
   - **TEST_ENVIRONMENT:** Select `staging`
   - **SKIP_TESTS:** Leave unchecked (❌)
   - **SEND_EMAIL:** Check if you want email (✅)
   - Click **Build**

### Step 3: Watch Build Progress
1. Look at **Build History** (bottom left)
2. You should see **#1** appear with a progress bar
3. Click on **#1**
4. Click **Console Output** to see live logs

### Step 4: Wait for Completion
- Build typically takes 30-60 seconds
- Watch the console output scroll
- Wait for final message

**Possible Results:**
- ✅ **SUCCESS** - All tests passed!
- ⚠️ **UNSTABLE** - Some tests failed (this is expected - 3 tests expose API bugs)
- ❌ **FAILURE** - Build error (check console output)

---

## 📊 PHASE 6: View Test Reports (2 minutes)

### After Build Completes:

1. **Go back to build page** (click `#1` in build history)

2. **View Extent Report (with highlighted order numbers):**
   - Click **MLX API Test Report (Highlighted Orders)**
   - See beautiful HTML report with:
     - 🟢 Green boxes for successful orders
     - 🔴 Red boxes for failed validations
     - Complete test execution details

3. **View JUnit Test Results:**
   - Click **Test Result**
   - See test statistics
   - Check which tests passed/failed
   - View test trends

4. **View Console Output:**
   - Click **Console Output**
   - See complete build logs
   - Debug if needed

---

## ✅ Success Criteria

**You have successfully set up CI if:**
- ✅ Build completes (SUCCESS or UNSTABLE)
- ✅ Console shows: "Finished: SUCCESS" or "Finished: UNSTABLE"
- ✅ Test report is accessible and shows highlighted order numbers
- ✅ All 18 tests executed
- ✅ Expected results:
  - 15 tests PASSED ✅
  - 3 tests FAILED ❌ (these are EXPECTED - they expose API bugs)

---

## 🔧 Troubleshooting Common Issues

### Issue 1: "Failed to connect to repository"
**Solution:**
- Verify GitHub token is correct
- Check repository URL is exactly: `https://github.com/vijaykumar0025/MLX_API_Automation.git`
- Ensure token has `repo` permissions
- Try regenerating token

### Issue 2: "Maven not found" or "JDK not found"
**Solution:**
- Go to Manage Jenkins → Global Tool Configuration
- Verify Maven name is exactly: `Maven-3.9.11`
- Verify JDK name is exactly: `JDK-11`
- Check "Install automatically" is checked
- Click Save and retry build

### Issue 3: "Jenkinsfile not found"
**Solution:**
- Verify Script Path is: `Jenkinsfile.CI-ONLY`
- Check the file exists in your GitHub repository
- Verify branch is correct (`main` vs `master`)

### Issue 4: "Permission denied" during checkout
**Solution:**
- Recreate GitHub token with correct permissions
- Update credentials in Jenkins
- Verify username is correct: `vijaykumar0025`

### Issue 5: Build stuck at "pending" or "queued"
**Solution:**
- Jenkins may be installing tools (first time)
- Wait 5-10 minutes
- Check Manage Jenkins → Manage Nodes and Clouds
- Ensure "master" node is online

### Issue 6: No HTML Report visible
**Solution:**
- Go to Manage Jenkins → Configure Global Security
- Scroll to "Markup Formatter"
- Select "Safe HTML" or "Plain text"
- Click Save
- Re-run build

---

## 📧 Optional: Configure Email Notifications

### Step 1: Configure SMTP
1. Go to **Manage Jenkins → Configure System**
2. Scroll to **Extended E-mail Notification**
3. Configure:
   - **SMTP server:** `smtp.gmail.com`
   - **SMTP Port:** `587`
   - ✅ Use SSL
   - **Default user e-mail suffix:** `@gmail.com`
   - **Credentials:** Add your email credentials
     - For Gmail: Use App Password, not regular password
   - **Default Recipients:** Your email address

### Step 2: Update Jenkinsfile
1. Edit `Jenkinsfile.CI-ONLY` in your repository
2. Line 13, change:
   ```groovy
   EMAIL_RECIPIENTS = 'your.email@company.com'
   ```
3. Commit and push to GitHub
4. Jenkins will pick it up on next build

---

## 🎉 What You've Accomplished!

**✅ Completed Steps:**
1. Logged into Jenkins ✓
2. Installed required plugins ✓
3. Configured Maven and JDK ✓
4. Created GitHub Personal Access Token ✓
5. Created Jenkins Pipeline Job ✓
6. Ran first build ✓
7. Viewed test reports ✓

**✅ What You Now Have:**
- Automated CI pipeline running in Jenkins
- Beautiful HTML reports with highlighted order numbers
- Test execution every time you push code (if webhooks enabled)
- Or scheduled daily runs
- Complete visibility of API test results

**✅ Safety Confirmed:**
- No deployment configured ✓
- Only runs tests ✓
- No impact on staging/dev environments ✓
- 100% safe to run ✓

---

## 🚀 Next Steps

### This Week:
- Run builds manually a few times
- Review test reports
- Understand the pipeline stages
- Get comfortable with Jenkins interface

### Next Week:
- Set up GitHub webhooks for auto-triggering
- Configure email notifications
- Add more team members

### Later:
- When ready, we can help you enable Continuous Deployment
- Start with staging environment
- Add manual approval gates
- Gradually move to production

---

## 📞 Need Help?

**If you get stuck:**
1. Check the Troubleshooting section above
2. Review Console Output for error messages
3. Check Jenkins logs: Manage Jenkins → System Log
4. Review the documentation files in your repository

**Common Questions:**
- "Where is Jenkins installed?" - Usually `C:\Program Files\Jenkins`
- "How do I stop Jenkins?" - Windows Services → Jenkins → Stop
- "How do I restart Jenkins?" - `http://localhost:8080/restart`
- "Can I run this on a different port?" - Yes, configure in Jenkins service

---

## 📚 Additional Resources

**In Your Repository:**
- `START_HERE.md` - Overview and introduction
- `INTEGRATION_GUIDE.md` - Complete integration guide
- `SAFE_CI_SETUP_GUIDE.md` - Safety-focused guide
- `BACKEND_STAGING_GUIDE.md` - Backend monitoring guide

**Jenkins Official:**
- Jenkins Documentation: https://www.jenkins.io/doc/
- Pipeline Syntax: https://www.jenkins.io/doc/book/pipeline/syntax/

---

## ✨ Congratulations!

You've successfully set up your first Jenkins CI pipeline! 🎉

Your MLX API Automation tests are now running automatically with beautiful reports and zero risk to your environments.

**Time spent:** ~30 minutes  
**Value gained:** Automated testing, professional reports, CI/CD foundation  
**Risk level:** 🟢 Zero (CI-only, no deployment)

---

**Happy Testing! 🚀**

Last Updated: January 9, 2026
