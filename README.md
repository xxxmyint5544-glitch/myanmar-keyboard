# မြန်မာကီးဘုတ် — ဖုန်းတစ်လုံးတည်းနဲ့ Build လုပ်နည်း

Computer/Android Studio မလိုအပ်ဘဲ GitHub Actions (အခမဲ့ cloud build) ကို သုံးပြီး
APK ထုတ်ယူနည်း အဆင့်ဆင့်။

## လိုအပ်တာများ
- Android ဖုန်း
- Internet
- **Termux** app (F-Droid ကနေ install ရင် ပိုကောင်း — https://f-droid.org/en/packages/com.termux/)
- **GitHub** account (အခမဲ့ — https://github.com/signup)

## အဆင့် ၁ — GitHub Repository ဖန်တီးခြင်း
1. github.com ကို browser နဲ့ဝင်ပြီး login ဝင်ပါ
2. အပေါ်ညာဘက် "+" → "New repository" ကိုနှိပ်ပါ
3. Repository name: `myanmar-keyboard` (သို့) ကြိုက်တဲ့နာမည်
4. **Public** ရွေးပြီး "Create repository" နှိပ်ပါ
5. ဒီ repo URL ကို မှတ်ထားပါ — `https://github.com/<your-username>/myanmar-keyboard`

## အဆင့် ၂ — Personal Access Token ယူခြင်း (password အစား သုံးရန်)
1. GitHub → Settings → Developer settings → Personal access tokens → Tokens (classic)
2. "Generate new token (classic)" နှိပ်ပါ
3. Scope: `repo` ကို check လုပ်ပါ
4. Generate ပြီးရင် token ကို copy ကူးထားပါ (တစ်ခါပဲပြမှာဖြစ်လို့ သေချာသိမ်းထားပါ)

## အဆင့် ၃ — Termux ထဲမှာ Setup လုပ်ခြင်း
Termux ကိုဖွင့်ပြီး တစ်ကြောင်းချင်း run ပါ:

```
pkg update -y
pkg install git unzip -y
termux-setup-storage
```
(termux-setup-storage run ရင် permission popup တက်လာရင် Allow နှိပ်ပါ)

## အဆင့် ၄ — Project ကို Extract လုပ်ပြီး GitHub ပေါ်တင်ခြင်း
Zip ဖိုင်ကို ဖုန်းရဲ့ Download folder ထဲထားပြီးရင်:

```
cd storage/downloads
unzip MyanmarKeyboard-Phase1.zip -d myanmar-keyboard
cd myanmar-keyboard
git init
git add .
git commit -m "Phase 1"
git branch -M main
git remote add origin https://<TOKEN>@github.com/<your-username>/myanmar-keyboard.git
git push -u origin main
```

`<TOKEN>` နဲ့ `<your-username>` နေရာမှာ ကိုယ့်ရဲ့ token/username အစားထိုးပါ။

## အဆင့် ၅ — Build ကို စောင့်ကြည့်ခြင်း
1. Browser နဲ့ `https://github.com/<your-username>/myanmar-keyboard/actions` ကိုသွားပါ
2. Workflow run တစ်ခု running/queued ဖြစ်နေတာ တွေ့ရမယ် (push တိုင်း auto run ဖြစ်တယ်)
3. ၂-၅ မိနစ်လောက်စောင့်ပါ (green tick ✅ ပြရင် အောင်မြင်ပြီ)
4. အဲ့ workflow run ကို click ဝင်ပါ → အောက်ဆုံးက "Artifacts" section မှာ
   `myanmar-keyboard-debug-apk` ကို download ဆွဲပါ

## အဆင့် ၆ — Install လုပ်ခြင်း
1. Download ရလာတဲ့ zip ကို extract လုပ်ပါ (`app-debug.apk` ပါလာမယ်)
2. ဖုန်းက file manager နဲ့ apk ကို နှိပ်ပြီး install လုပ်ပါ
3. "Install unknown apps" permission တောင်းရင် Allow ပေးပါ
4. App ကိုဖွင့်ပြီး "Enable Keyboard" → "Switch to Myanmar Keyboard" နှိပ်ပါ

---
နောက်တစ်ကြိမ် code ပြောင်းရင် (Phase 2, 3...) အလားတူပဲ
`git add . && git commit -m "..." && git push` လုပ်လိုက်ရုံနဲ့ APK အသစ် auto-build ထွက်ပါလိမ့်မယ်။
