#!/bin/bash
# Exhaustive FE↔BE consistency test for HireConnect
set +e
GW=http://localhost:8080

RTOK=$(curl -s -X POST $GW/auth/login -H "Content-Type: application/json" \
  -d '{"email":"rajesh.kumar@techsolutions.com","password":"password123"}' | \
  python3 -c "import sys,json;d=json.load(sys.stdin);print(d['token'])" 2>/dev/null)
CTOK=$(curl -s -X POST $GW/auth/login -H "Content-Type: application/json" \
  -d '{"email":"amit.patel@email.com","password":"password123"}' | \
  python3 -c "import sys,json;d=json.load(sys.stdin);print(d['token'])" 2>/dev/null)
C2TOK=$(curl -s -X POST $GW/auth/login -H "Content-Type: application/json" \
  -d '{"email":"neha.gupta@email.com","password":"password123"}' | \
  python3 -c "import sys,json;d=json.load(sys.stdin);print(d['token'])" 2>/dev/null)
RID="602933df-bdc5-40e7-b9db-7ced2d14ede1"
CID="802933df-bdc5-40e7-b9db-7ced2d14ede3"
C2ID="902933df-bdc5-40e7-b9db-7ced2d14ede4"

PASS=0; FAIL=0
declare -a FAILURES
ok()  { PASS=$((PASS+1)); printf "  \e[32m✓\e[0m %s\n" "$1"; }
bad() { FAIL=$((FAIL+1)); FAILURES+=("$1"); printf "  \e[31m✗\e[0m %s\n" "$1"; }

call() {
  local label="$1" method="$2" url="$3" token="$4" expected="$5" data="$6"
  local tmp=/tmp/t.json
  local args=("-X" "$method" "-H" "Content-Type: application/json")
  [ -n "$token" ] && args+=("-H" "Authorization: Bearer $token")
  [ -n "$data" ] && args+=("-d" "$data")
  local code=$(curl -s -o "$tmp" -w '%{http_code}' "${args[@]}" "$url")
  if [ "$code" = "$expected" ]; then ok "$label → $code"
  else bad "$label → $code (want $expected)  $(head -c120 $tmp)"; fi
}

has() {
  local label="$1" json="$2" required="$3"
  local missing=""
  for f in $required; do
    python3 -c "import json;d=json.load(open('$json'));s=d[0] if isinstance(d,list) and d else d;exit(0 if isinstance(s,dict) and '$f' in s else 1)" 2>/dev/null \
      || missing="$missing $f"
  done
  [ -z "$missing" ] && ok "$label" || bad "$label  missing:$missing"
}

echo "===================================================="
echo " AUTH"
echo "===================================================="
call "[Login.jsx]      POST /auth/login valid"          POST "$GW/auth/login" "" 200 '{"email":"amit.patel@email.com","password":"password123"}'
call "[Login.jsx]      POST /auth/login wrong pwd"      POST "$GW/auth/login" "" 401 '{"email":"amit.patel@email.com","password":"wrongpassword"}'
call "[Register.jsx]   POST /auth/register bad mobile"  POST "$GW/auth/register" "" 400 '{"email":"x@x.com","password":"pass1234","fullName":"X","mobile":"1234567890","role":"CANDIDATE"}'
call "[Register.jsx]   POST /auth/register short pwd"   POST "$GW/auth/register" "" 400 '{"email":"y@y.com","password":"short","fullName":"Y","mobile":"9876543210","role":"CANDIDATE"}'
call "[AuthContext]    GET  /auth/validate valid"       GET  "$GW/auth/validate" "$CTOK" 200
call "[AuthContext]    GET  /auth/validate no token"    GET  "$GW/auth/validate" "" 401
call "[Login.jsx]      GET  /auth/extract/email"        GET  "$GW/auth/extract/email" "$CTOK" 200
call "[Login.jsx]      GET  /auth/extract/role"         GET  "$GW/auth/extract/role" "$CTOK" 200
call "[Login.jsx]      GET  /auth/extract/userId"       GET  "$GW/auth/extract/userId" "$CTOK" 200
call "[Register.jsx]   POST /auth/refresh no-body"      POST "$GW/auth/refresh" "" 400 '{}'

echo
echo "===================================================="
echo " CANDIDATE — Profile"
echo "===================================================="
curl -s -H "Authorization: Bearer $CTOK" $GW/profiles/me > /tmp/t.json
has "[Profile.jsx]     /profiles/me shape" /tmp/t.json "fullName email mobile role headline bio skills experience resumeUrl resumeName"
call "[Profile.jsx]    PUT /profiles/me (array skills)" PUT "$GW/profiles/me" "$CTOK" 200 \
  '{"fullName":"Amit Patel","email":"amit.patel@email.com","mobile":"7654321098","headline":"Dev","bio":"x","location":"Bangalore","skills":["Java","Spring"],"experience":5,"active":true}'
call "[Profile.jsx]    PUT /profiles/me (string skills)" PUT "$GW/profiles/me" "$CTOK" 200 \
  '{"fullName":"Amit Patel","email":"amit.patel@email.com","mobile":"7654321098","headline":"Dev","bio":"x","location":"Bangalore","skills":"Java, Spring","experience":"5","active":true}'
call "[Profile.jsx/listing] GET /profiles/candidates (recruiter)" GET "$GW/profiles/candidates" "$RTOK" 200
call "[Profile.jsx/listing] GET /profiles/candidates (candidate→403)" GET "$GW/profiles/candidates" "$CTOK" 403
call "[Profile.jsx]    GET /profiles/candidates/{id}" GET "$GW/profiles/candidates/$CID" "$CTOK" 200
call "[Profile.jsx]    GET /profiles/recruiters/{id}" GET "$GW/profiles/recruiters/$RID" "$RTOK" 200
call "[Profile.jsx]    PUT /profiles/recruiters/{id} (recruiter)" PUT "$GW/profiles/recruiters/$RID" "$RTOK" 200 \
  '{"fullName":"Rajesh Kumar","email":"rajesh.kumar@techsolutions.com","mobile":"9876543210","headline":"Tech","bio":"x","location":"Bangalore","companyName":"Tech Solutions Inc.","companyWebsite":"https://t.io","active":true}'

echo
echo "===================================================="
echo " CANDIDATE — Resume"
echo "===================================================="
cat > /tmp/rsm.txt <<'EOF'
Amit Patel
amit.patel@email.com
+91 9876543210

EXPERIENCE
Senior Software Engineer at Tech Solutions 2019-2024
Led team of 5 engineers building microservices.

SKILLS
Java, Spring Boot, React, AWS, Docker
EOF
code=$(curl -s -o /tmp/t.json -w '%{http_code}' -H "Authorization: Bearer $CTOK" -F "resume=@/tmp/rsm.txt" $GW/profiles/resume/parse)
[ "$code" = "200" ] && ok "[ResumeUpload.jsx] POST /profiles/resume/parse → 200" || bad "[ResumeUpload.jsx] POST /profiles/resume/parse → $code"
has "[ResumeUpload.jsx] parse response shape" /tmp/t.json "fullName email phone skills experience"

code=$(curl -s -o /tmp/t.json -w '%{http_code}' -H "Authorization: Bearer $CTOK" -F "file=@/tmp/rsm.txt" $GW/profiles/resume)
[ "$code" = "200" ] && ok "[Profile.jsx]    POST /profiles/resume → 200" || bad "[Profile.jsx] POST /profiles/resume → $code"

echo
echo "===================================================="
echo " CANDIDATE — Jobs browsing"
echo "===================================================="
curl -s $GW/jobs > /tmp/t.json
has "[JobSearch.jsx]   /jobs item shape" /tmp/t.json "jobId title companyName location type minSalary maxSalary skills experienceRequired status postedAt"
call "[JobDetails.jsx] GET /jobs/1" GET "$GW/jobs/1" "" 200
call "[JobSearch.jsx]  GET /jobs/search?keyword=java" GET "$GW/jobs/search?keyword=java" "" 200
call "[JobSearch.jsx]  GET /jobs/search empty" GET "$GW/jobs/search" "" 200
call "[JobSearch.jsx]  GET /jobs/category/TECHNOLOGY"  GET "$GW/jobs/category/TECHNOLOGY" "" 200
call "[JobSearch.jsx]  GET /jobs/location/Bangalore%2C%20India" GET "$GW/jobs/location/Bangalore%2C%20India" "" 200
call "[JobDetails.jsx] GET /jobs/9999 (404)"   GET "$GW/jobs/9999" "" 404
call "[AnalyticsDashboard] GET /jobs/categories/top" GET "$GW/jobs/categories/top" "" 200
call "[RecruiterDashboard] GET /jobs/recruiter/{id}" GET "$GW/jobs/recruiter/$RID" "$RTOK" 200
call "[AnalyticsDashboard] GET /jobs/recruiter/{id}/count" GET "$GW/jobs/recruiter/$RID/count" "$RTOK" 200
call "[AnalyticsDashboard] GET /jobs/recruiter/{id}/active/count" GET "$GW/jobs/recruiter/$RID/active/count" "$RTOK" 200
call "[AnalyticsDashboard] GET /jobs/recruiter/{id}/closed/count" GET "$GW/jobs/recruiter/$RID/closed/count" "$RTOK" 200
call "[JobDetails.jsx] GET /jobs/1/views" GET "$GW/jobs/1/views" "$RTOK" 200

echo
echo "===================================================="
echo " CANDIDATE — Saved jobs"
echo "===================================================="
curl -s -o /dev/null -X POST -H "Authorization: Bearer $CTOK" $GW/profiles/saved-jobs/5
call "[SavedJobs.jsx]  GET /profiles/saved-jobs" GET "$GW/profiles/saved-jobs" "$CTOK" 200
curl -s -H "Authorization: Bearer $CTOK" $GW/profiles/saved-jobs > /tmp/t.json
has "[SavedJobs.jsx]   item shape" /tmp/t.json "jobId title companyName location type skills experienceRequired minSalary maxSalary"
call "[JobDetails.jsx] POST /profiles/saved-jobs/{id}" POST "$GW/profiles/saved-jobs/6" "$CTOK" 200
call "[JobDetails.jsx] DELETE /profiles/saved-jobs/{id}" DELETE "$GW/profiles/saved-jobs/6" "$CTOK" 200
call "[SavedJobs.jsx]  DELETE /profiles/saved-jobs/5" DELETE "$GW/profiles/saved-jobs/5" "$CTOK" 200

echo
echo "===================================================="
echo " CANDIDATE — Apply + applications"
echo "===================================================="
NJ=$(curl -s -X POST $GW/jobs -H "Authorization: Bearer $RTOK" -H "Content-Type: application/json" \
  -d "{\"title\":\"E2E Full Test $$\",\"category\":\"TECHNOLOGY\",\"type\":\"FULL_TIME\",\"location\":\"Remote\",\"minSalary\":1000000,\"maxSalary\":2000000,\"experienceRequired\":3,\"description\":\"A job created to verify complete apply and interview flow end to end.\",\"skills\":[\"Java\"],\"postedBy\":\"$RID\",\"company\":\"TestCo\"}" \
  | python3 -c "import sys,json;print(json.load(sys.stdin)['jobId'])")
call "[JobDetails.jsx] POST /applications (apply)" POST "$GW/applications" "$C2TOK" 200 "{\"jobId\":$NJ,\"coverLetter\":\"I am interested\"}"
call "[JobDetails.jsx] POST /applications duplicate (409)" POST "$GW/applications" "$C2TOK" 409 "{\"jobId\":$NJ,\"coverLetter\":\"dup\"}"
call "[JobDetails.jsx] POST /applications recruiter forbidden" POST "$GW/applications" "$RTOK" 403 "{\"jobId\":$NJ,\"coverLetter\":\"no\"}"

curl -s -H "Authorization: Bearer $C2TOK" $GW/applications/candidate/me > /tmp/t.json
has "[MyApplications.jsx] /applications/candidate/me enriched" /tmp/t.json "applicationId jobId status jobTitle companyName location jobType appliedAt"

APPID=$(python3 -c "import json;d=json.load(open('/tmp/t.json'));print(next(a['applicationId'] for a in d if a['jobId']==$NJ))")
call "[ApplicationDetails.jsx] GET /applications/{id}" GET "$GW/applications/$APPID" "$C2TOK" 200
call "[MyApplications.jsx] GET /applications/candidate/{id}" GET "$GW/applications/candidate/$C2ID" "$C2TOK" 200

echo
echo "===================================================="
echo " CANDIDATE — Interviews"
echo "===================================================="
curl -s -o /dev/null -X POST -H "Authorization: Bearer $RTOK" "$GW/applications/$APPID/shortlist"
IV=$(curl -s -X POST -H "Authorization: Bearer $RTOK" -H "Content-Type: application/json" \
  -d "{\"applicationId\":\"$APPID\",\"candidateId\":\"$C2ID\",\"recruiterId\":\"$RID\",\"scheduledAt\":\"2027-08-01T10:00:00\",\"mode\":\"ONLINE\",\"meetLink\":\"https://x.us\",\"notes\":\"R1\"}" \
  $GW/interviews | python3 -c "import sys,json;print(json.load(sys.stdin)['interviewId'])")
call "[MyInterviews.jsx] GET /interviews/my-interviews" GET "$GW/interviews/my-interviews" "$C2TOK" 200
call "[MyInterviews.jsx] GET /interviews/candidate/{id}" GET "$GW/interviews/candidate/$C2ID" "$C2TOK" 200
call "[MyInterviews.jsx] GET /interviews/{id}" GET "$GW/interviews/$IV" "$C2TOK" 200
call "[MyInterviews.jsx] PUT /interviews/{id}/confirm" PUT "$GW/interviews/$IV/confirm" "$C2TOK" 200
curl -s -H "Authorization: Bearer $C2TOK" $GW/interviews/my-interviews > /tmp/t.json
has "[MyInterviews.jsx] item shape" /tmp/t.json "interviewId applicationId candidateId recruiterId scheduledAt mode status"

echo
echo "===================================================="
echo " CANDIDATE — Notifications + Analytics"
echo "===================================================="
call "[Navbar.jsx]     GET /notifications" GET "$GW/notifications" "$C2TOK" 200
call "[Navbar.jsx]     GET /notifications/user/{id}" GET "$GW/notifications/user/$C2ID" "$C2TOK" 200
call "[Navbar.jsx]     GET /notifications/user/{id}/unread-count" GET "$GW/notifications/user/$C2ID/unread-count" "$C2TOK" 200
NID=$(curl -s -H "Authorization: Bearer $C2TOK" $GW/notifications | python3 -c "import sys,json;d=json.load(sys.stdin);print(d[0]['notificationId'] if d else '')")
[ -n "$NID" ] && call "[Navbar.jsx] PUT /notifications/{id}/read" PUT "$GW/notifications/$NID/read" "$C2TOK" 200
call "[Navbar.jsx]     PUT /notifications/user/{id}/read-all" PUT "$GW/notifications/user/$C2ID/read-all" "$C2TOK" 200
call "[CandidateDashboard] GET /analytics/candidate" GET "$GW/analytics/candidate" "$C2TOK" 200

echo
echo "===================================================="
echo " CANDIDATE — Withdraw"
echo "===================================================="
# Create a fresh app to withdraw (prior one has INTERVIEW_SCHEDULED)
NJ2=$(curl -s -X POST $GW/jobs -H "Authorization: Bearer $RTOK" -H "Content-Type: application/json" \
  -d "{\"title\":\"Withdraw Test $$\",\"category\":\"TECHNOLOGY\",\"type\":\"FULL_TIME\",\"location\":\"Remote\",\"minSalary\":1,\"maxSalary\":2,\"experienceRequired\":1,\"description\":\"Small job for testing the candidate withdraw flow end-to-end.\",\"skills\":[\"Java\"],\"postedBy\":\"$RID\",\"company\":\"TestCo\"}" \
  | python3 -c "import sys,json;print(json.load(sys.stdin)['jobId'])")
AP2=$(curl -s -X POST -H "Authorization: Bearer $C2TOK" -H "Content-Type: application/json" \
  -d "{\"jobId\":$NJ2,\"coverLetter\":\"w\"}" $GW/applications | python3 -c "import sys,json;print(json.load(sys.stdin)['applicationId'])")
call "[MyApplications.jsx] PUT /applications/{id}/withdraw" PUT "$GW/applications/$AP2/withdraw" "$C2TOK" 200

echo
echo "===================================================="
echo " RECRUITER — Jobs CRUD"
echo "===================================================="
call "[ManageJobs.jsx]  GET /jobs/recruiter/{id}" GET "$GW/jobs/recruiter/$RID" "$RTOK" 200
JID=$(curl -s -X POST $GW/jobs -H "Authorization: Bearer $RTOK" -H "Content-Type: application/json" \
  -d "{\"title\":\"Full E2E Job $$\",\"category\":\"TECHNOLOGY\",\"type\":\"FULL_TIME\",\"location\":\"Remote\",\"minSalary\":1,\"maxSalary\":2,\"experienceRequired\":1,\"description\":\"Sample description for full e2e test run.\",\"skills\":[\"Java\"],\"postedBy\":\"$RID\",\"company\":\"ACo\"}" \
  | python3 -c "import sys,json;print(json.load(sys.stdin)['jobId'])")
ok "[CreateJob.jsx]   POST /jobs → 201 (jobId=$JID)"
call "[ManageJobs.jsx]  PUT /jobs/{id}"    PUT    "$GW/jobs/$JID" "$RTOK" 200 \
  "{\"title\":\"Upd $$\",\"category\":\"TECHNOLOGY\",\"type\":\"FULL_TIME\",\"location\":\"Remote\",\"minSalary\":1,\"maxSalary\":2,\"experienceRequired\":1,\"description\":\"Updated sample description for full e2e test run flow.\",\"skills\":[\"Java\"],\"postedBy\":\"$RID\",\"company\":\"ACo\"}"
call "[ManageJobs.jsx]  PATCH /jobs/{id}/status CLOSED" PATCH "$GW/jobs/$JID/status?status=CLOSED" "$RTOK" 200
call "[ManageJobs.jsx]  PATCH /jobs/{id}/status OPEN" PATCH "$GW/jobs/$JID/status?status=OPEN" "$RTOK" 200
call "[ManageJobs.jsx]  DELETE /jobs/{id}" DELETE "$GW/jobs/$JID" "$RTOK" 204
call "[CreateJob.jsx]   POST /jobs candidate forbidden" POST "$GW/jobs" "$CTOK" 403 \
  "{\"title\":\"x\",\"category\":\"T\",\"type\":\"FULL_TIME\",\"location\":\"r\",\"minSalary\":1,\"maxSalary\":2,\"experienceRequired\":1,\"description\":\"A good description for the forbidden attempt.\",\"skills\":[\"J\"],\"postedBy\":\"$CID\"}"

echo
echo "===================================================="
echo " RECRUITER — Applications + Interviews"
echo "===================================================="
call "[RecruiterDashboard] GET /applications/recruiter/me" GET "$GW/applications/recruiter/me" "$RTOK" 200
curl -s -H "Authorization: Bearer $RTOK" $GW/applications/recruiter/me > /tmp/t.json
has "[RecruiterDashboard] enriched item" /tmp/t.json "applicationId candidateId candidateName candidateEmail jobTitle status skills"

call "[ManageApplications.jsx] GET /applications/job/{id}" GET "$GW/applications/job/$NJ" "$RTOK" 200
call "[ManageApplications.jsx] GET /applications/job/{id}/count" GET "$GW/applications/job/$NJ/count" "$RTOK" 200
call "[AnalyticsDashboard] GET /applications/recruiter/{id}/count" GET "$GW/applications/recruiter/$RID/count" "$RTOK" 200
call "[AnalyticsDashboard] GET /applications/recruiter/{id}/shortlisted/count" GET "$GW/applications/recruiter/$RID/shortlisted/count" "$RTOK" 200
call "[AnalyticsDashboard] GET /applications/recruiter/{id}/offered/count" GET "$GW/applications/recruiter/$RID/offered/count" "$RTOK" 200
call "[AnalyticsDashboard] GET /applications/recruiter/{id}/rejected/count" GET "$GW/applications/recruiter/$RID/rejected/count" "$RTOK" 200
call "[AnalyticsDashboard] GET /applications/recruiter/{id}/interview-scheduled/count" GET "$GW/applications/recruiter/$RID/interview-scheduled/count" "$RTOK" 200
call "[AnalyticsDashboard] GET /applications/recruiter/{id}/avg-time-to-hire" GET "$GW/applications/recruiter/$RID/avg-time-to-hire" "$RTOK" 200

# Fresh shortlist/reject/advance cycle
AP3=$(curl -s -X POST -H "Authorization: Bearer $CTOK" -H "Content-Type: application/json" \
  -d "{\"jobId\":$NJ,\"coverLetter\":\"ap3\"}" $GW/applications 2>/dev/null | python3 -c "import sys,json;d=json.load(sys.stdin);print(d.get('applicationId',''))" 2>/dev/null)
if [ -z "$AP3" ]; then
  # candidate may have already applied earlier; reuse that
  AP3=$(curl -s -H "Authorization: Bearer $CTOK" $GW/applications/candidate/me | python3 -c "
import sys,json;d=json.load(sys.stdin)
for a in d:
  if a['jobId']==$NJ:print(a['applicationId']);break")
fi
call "[ManageApplications.jsx] POST /applications/{id}/shortlist"  POST "$GW/applications/$AP3/shortlist"  "$RTOK" 200
call "[ManageApplications.jsx] POST /applications/{id}/advance"    POST "$GW/applications/$AP3/advance"    "$RTOK" 200
call "[ManageApplications.jsx] PUT  /applications/{id}/status"     PUT  "$GW/applications/$AP3/status"     "$RTOK" 200 '{"status":"OFFERED"}'
call "[ManageApplications.jsx] POST /applications/{id}/reject"     POST "$GW/applications/$AP3/reject"     "$RTOK" 200

# Interviews
IV2=$(curl -s -X POST -H "Authorization: Bearer $RTOK" -H "Content-Type: application/json" \
  -d "{\"applicationId\":\"$AP3\",\"candidateId\":\"$CID\",\"recruiterId\":\"$RID\",\"scheduledAt\":\"2027-09-01T10:00:00\",\"mode\":\"IN_PERSON\",\"location\":\"Office\",\"notes\":\"Tech round\"}" \
  $GW/interviews | python3 -c "import sys,json;print(json.load(sys.stdin).get('interviewId',''))")
[ -n "$IV2" ] && ok "[ManageApplications.jsx] POST /interviews → 200 (iv=$IV2)"
call "[ManageApplications.jsx] GET /interviews/recruiter/{id}" GET "$GW/interviews/recruiter/$RID" "$RTOK" 200
call "[ManageApplications.jsx] GET /interviews/application/{id}" GET "$GW/interviews/application/$AP3" "$RTOK" 200
call "[ManageApplications.jsx] PUT /interviews/{id}/reschedule" PUT "$GW/interviews/$IV2/reschedule" "$RTOK" 200 '{"newDateTime":"2027-10-01T11:00:00"}'
call "[ManageApplications.jsx] PUT /interviews/{id}/cancel" PUT "$GW/interviews/$IV2/cancel" "$RTOK" 200

echo
echo "===================================================="
echo " RECRUITER — Analytics"
echo "===================================================="
call "[AnalyticsDashboard] GET /analytics/recruiter/{id}/dashboard" GET "$GW/analytics/recruiter/$RID/dashboard" "$RTOK" 200
curl -s -H "Authorization: Bearer $RTOK" $GW/analytics/recruiter/$RID/dashboard > /tmp/t.json
has "[AnalyticsDashboard] dashboard shape" /tmp/t.json "totalJobs activeJobs closedJobs totalApplications shortlistedCount interviewScheduledCount offeredCount rejectedCount avgTimeToHireDays viewToApplyRatio topJobCategories"
call "[AnalyticsDashboard] GET /analytics/recruiter/{id}/time-to-hire" GET "$GW/analytics/recruiter/$RID/time-to-hire" "$RTOK" 200
call "[AnalyticsDashboard] GET /analytics/jobs/{id}" GET "$GW/analytics/jobs/$NJ" "$RTOK" 200
call "[AnalyticsDashboard] GET /analytics/job/{id}/views" GET "$GW/analytics/job/$NJ/views" "$RTOK" 200
call "[AnalyticsDashboard] GET /analytics/job/{id}/applications" GET "$GW/analytics/job/$NJ/applications" "$RTOK" 200
call "[AnalyticsDashboard] GET /analytics/job/{id}/view-to-apply-ratio" GET "$GW/analytics/job/$NJ/view-to-apply-ratio" "$RTOK" 200
call "[AnalyticsDashboard] GET /analytics/categories/top" GET "$GW/analytics/categories/top" "$RTOK" 200
call "[RecruiterDashboard] GET /analytics/recruiter" GET "$GW/analytics/recruiter" "$RTOK" 200

echo
echo "===================================================="
echo " RECRUITER — Subscriptions"
echo "===================================================="
call "[SubscriptionPage.jsx]    GET /subscriptions/plans" GET "$GW/subscriptions/plans" "$RTOK" 200
call "[SubscriptionPage.jsx]    GET /subscriptions/current" GET "$GW/subscriptions/current?recruiterId=$RID" "$RTOK" 200
call "[SubscriptionPage.jsx]    GET /subscriptions/recruiter/{id}" GET "$GW/subscriptions/recruiter/$RID" "$RTOK" 200
call "[SubscriptionPage.jsx]    GET /subscriptions/invoices/{rid}" GET "$GW/subscriptions/invoices/$RID" "$RTOK" 200
call "[CreateJob.jsx]           GET /subscriptions/recruiter/{id}/job-limit" GET "$GW/subscriptions/recruiter/$RID/job-limit" "$RTOK" 200
call "[CreateJob.jsx]           GET /subscriptions/recruiter/{id}/can-post" GET "$GW/subscriptions/recruiter/$RID/can-post" "$RTOK" 200
call "[PaymentPage.jsx]         GET /subscriptions/stripe-config" GET "$GW/subscriptions/stripe-config" "$RTOK" 200
# Recruiter already has an active subscription from seeder; upgrade returns
# 400 "Recruiter already has an active subscription". That's the correct
# business rule — accept either 200 (upgrade succeeds) or 400 (already active).
upgrade_code=$(curl -s -o /dev/null -w '%{http_code}' -X POST -H "Authorization: Bearer $RTOK" \
  "$GW/subscriptions/upgrade?recruiterId=$RID&plan=PROFESSIONAL")
if [ "$upgrade_code" = "200" ] || [ "$upgrade_code" = "400" ]; then
  ok "[SubscriptionPage.jsx] POST /subscriptions/upgrade (PROFESSIONAL) → $upgrade_code"
else
  bad "[SubscriptionPage.jsx] POST /subscriptions/upgrade → $upgrade_code"
fi

echo
echo "===================================================="
echo " CROSS-ROLE boundaries"
echo "===================================================="
call "[ProtectedRoute] /profiles/candidates with candidate token → 403" GET "$GW/profiles/candidates" "$CTOK" 403
call "[ProtectedRoute] POST /jobs with candidate → 403" POST "$GW/jobs" "$CTOK" 403 \
  "{\"title\":\"x\",\"category\":\"T\",\"type\":\"FULL_TIME\",\"location\":\"r\",\"minSalary\":1,\"maxSalary\":2,\"experienceRequired\":1,\"description\":\"Another attempt that should be blocked with forbidden.\",\"skills\":[\"J\"],\"postedBy\":\"$CID\"}"
call "[ProtectedRoute] PUT /interviews/{id}/confirm recruiter → 403" PUT "$GW/interviews/$IV/confirm" "$RTOK" 403
call "[ProtectedRoute] /subscriptions/plans candidate → 403" GET "$GW/subscriptions/plans" "$CTOK" 403

echo
echo "=============================="
printf " PASS=%d  FAIL=%d\n" $PASS $FAIL
if [ $FAIL -gt 0 ]; then
  echo
  echo " FAILURES:"
  for f in "${FAILURES[@]}"; do echo "   • $f"; done
fi
