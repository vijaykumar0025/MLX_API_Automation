# 🎯 Backend Staging Branch - Quick Setup Guide

## ✅ Updated for Staging Branch

I've specifically configured everything for the **staging branch** of your backend repository:

**Repository:** `https://github.com/LabsquireTech/api-mobilelabexpress.com`  
**Branch:** `staging` ⚠️ **IMPORTANT**

---

## 📁 What's Been Created

### New File: `Jenkinsfile.Backend-Staging`
A Jenkins pipeline specifically for monitoring your staging branch that:
- ✅ Checks out the **staging branch** (not dev, not main)
- ✅ Analyzes repository structure
- ✅ Shows commit information
- ✅ Monitors for changes
- ❌ Does NOT deploy anything
- ❌ Does NOT modify staging environment
- ❌ Does NOT build the application (yet)

---

## 🚀 Two Ways to Set This Up

### Option A: Use Jenkins Pipeline Script (RECOMMENDED - No Backend Changes)

**Advantages:**
- ✅ No changes needed to backend repository
- ✅ Safer approach
- ✅ Backend team doesn't see any new files
- ✅ You maintain full control in Jenkins

**Steps:**
1. Create Jenkins job: `MLX_Backend_Staging_Monitor`
2. Choose **Pipeline script** (not "from SCM")
3. Copy-paste contents of `Jenkinsfile.Backend-Staging` into Jenkins
4. Configure credentials for staging branch access
5. Done! ✅

### Option B: Add Jenkinsfile to Backend Repo

**Advantages:**
- ✅ Jenkinsfile version-controlled with code
- ✅ Team can see CI configuration

**Disadvantages:**
- ⚠️ Requires push access to staging branch
- ⚠️ Adds a file to the backend repository

**Steps:**
1. Get the `Jenkinsfile.Backend-Staging` file
2. Copy it to backend repo as `Jenkinsfile`
3. Commit to staging branch
4. Configure Jenkins to use it

---

## 🔒 Safety Features

### Staging Branch Protection
```groovy
BRANCH_NAME = 'staging'  // Explicitly set to staging
```

### No Deployment
```groovy
DEPLOY_ENABLED = 'false'  // Deployment disabled
```

### Read-Only Monitoring
- Only reads from staging branch
- Shows what's changed
- Displays commit information
- No modifications to environment

---

## 📊 What You'll See in Jenkins

When this pipeline runs:

```
✓ Stage 1: Checkout Staging Branch
  - Branch: staging
  - Latest commit info
  
✓ Stage 2: Repository Analysis
  - Detects if Node.js project
  - Lists important files
  - Shows project structure
  
✓ Stage 3: Build Verification (Optional)
  - Checks if npm is available
  - Ready for future build steps
  
✓ Stage 4: Code Quality (Placeholder)
  - Ready for ESLint, SonarQube
  - Not configured yet
  
✓ Stage 5: Commit Information
  - Shows who committed
  - Shows commit message
  - Shows commit date
  
✓ Stage 6: Deployment (DISABLED)
  - Just prints a message
  - Does nothing else
```

---

## 📧 Email Notifications

You'll get emails showing:
- **Repository:** api-mobilelabexpress.com
- **Branch:** staging (highlighted in yellow)
- **Build status:** Monitoring ✓
- **Latest commit:** First 8 characters of commit hash
- **Console output link**

---

## 🎯 Complete Setup Instructions

### Step 1: Push Updated Files (1 minute)

```cmd
cd C:\Users\VIJAY\eclipse-workspace\MLX_API_Automation

git add Jenkinsfile.Backend-Staging
git add INTEGRATION_GUIDE.md
git add BACKEND_STAGING_GUIDE.md

git commit -m "Add staging branch monitoring for backend repository"
git push origin main
```

### Step 2: Create GitHub Token for Backend Access (3 minutes)

Since this is the LabsquireTech repository (not your personal repo), you need a token with access to it:

1. Go to: https://github.com/settings/tokens
2. Click **Generate new token (classic)**
3. Settings:
   - **Note:** `Jenkins LabsquireTech Staging Monitor`
   - **Expiration:** 90 days
   - **Scopes:**
     - ✅ `repo` (to access the private repository)
4. Click **Generate token**
5. **Copy the token immediately!**

### Step 3: Create Jenkins Job (5 minutes)

1. **Jenkins Dashboard → New Item**
2. **Name:** `MLX_Backend_Staging_Monitor`
3. **Type:** Pipeline
4. Click **OK**

5. **General Settings:**
   - **Description:**
     ```
     Monitors api-mobilelabexpress.com STAGING BRANCH
     CI-ONLY - No Deployment
     Read-only monitoring of staging environment
     ```
   - ✅ Discard old builds
     - Max # of builds to keep: `10`

6. **Build Triggers:**
   - ✅ Poll SCM: `H/30 * * * *` (checks staging every 30 min)
   - Optional: ✅ Build periodically: `H 8 * * 1-5` (weekdays at 8 AM)

7. **Pipeline Configuration - CHOOSE YOUR OPTION:**

   **For Option A (Recommended):**
   - **Definition:** `Pipeline script`
   - **Script:** 
     1. Open `Jenkinsfile.Backend-Staging` in your editor
     2. Copy the entire contents
     3. Paste into the Pipeline script box in Jenkins
   - **Sandbox:** ✅ (checked)
   
   **For Option B (If using Jenkinsfile in backend repo):**
   - **Definition:** `Pipeline script from SCM`
   - **SCM:** `Git`
   - **Repository URL:** `https://github.com/LabsquireTech/api-mobilelabexpress.com`
   - **Credentials:** Click **Add**
     - Username: Your GitHub username
     - Password: [Token from Step 2]
     - ID: `github-labsquire-staging`
     - Description: `GitHub Token - LabsquireTech Staging`
   - **Branch Specifier:** `*/staging` ⚠️ **CRITICAL: Use staging**
   - **Script Path:** `Jenkinsfile`

8. **Update Email (in the script if Option A):**
   - Find line: `EMAIL_RECIPIENTS = 'your-email@example.com'`
   - Change to: `EMAIL_RECIPIENTS = 'your.email@company.com'`

9. Click **Save**

### Step 4: Test the Pipeline (2 minutes)

1. Click **Build Now** (or **Build with Parameters**)
2. Watch the console output
3. Verify it checks out the **staging** branch
4. Check email notification

---

## ✅ Verification Checklist

After first build, verify:
- [ ] Console shows: "Branch: staging" or "*/staging"
- [ ] No errors in checkout stage
- [ ] Repository analysis shows project files
- [ ] Deployment stage shows "SKIPPED"
- [ ] Email received (if configured)
- [ ] No changes to staging environment

---

## 🔧 Troubleshooting

### "Permission denied" error
**Fix:** Verify GitHub token has access to LabsquireTech organization

### "Branch not found" error  
**Fix:** Confirm the branch is named exactly `staging` in the backend repo

### No email received
**Fix:** Update EMAIL_RECIPIENTS in the Jenkinsfile script

### Build takes too long
**Fix:** Disable "Build Verification" parameter in next build

---

## 📈 What Happens Next?

### Week 1: Monitor Only
- Pipeline checks staging branch every 30 minutes
- You get email when changes detected
- No impact on staging environment

### Week 2-4: Observe Patterns
- See who's committing to staging
- Track commit frequency
- Understand deployment patterns

### Month 2+: Enable More Features (Optional)
- Add actual build verification
- Run backend tests (if they exist)
- Add code quality checks
- Eventually: Enable deployment (with approvals)

---

## 🎯 Summary

**What You're Setting Up:**
- Monitor the **staging branch** of api-mobilelabexpress.com
- Get notified of changes
- Track commits and committers
- Zero modifications to staging environment

**Safety Level:** 🟢 100% Safe
- Read-only access
- No deployment configured
- No build process (yet)
- Staging environment untouched

**Time Required:** 10 minutes
**Difficulty:** Easy (with this guide)

---

## 💡 Recommendation

**Start with Option A:**
1. Less risky (no changes to backend repo)
2. Faster to set up
3. Easier to modify later
4. No coordination needed with backend team

**Later, switch to Option B:**
- When you're comfortable with Jenkins
- When backend team approves
- When you want version-controlled Jenkinsfile

---

**Ready to set this up?** Follow Step 1 above! 🚀

Any questions about the staging branch setup? Just ask! 😊
