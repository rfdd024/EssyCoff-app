# EssyCoff POS Setup Guide

## Quick Start Guide

### 1. Supabase Setup

#### Step 1: Create Supabase Project
1. Go to [supabase.com](https://supabase.com) and create account
2. Create new project
3. Wait for project to be ready (2-3 minutes)

#### Step 2: Setup Database
1. Go to SQL Editor in Supabase dashboard
2. Copy and paste content from `supabase_setup.sql`
3. Run the SQL script
4. Verify tables are created in Table Editor

#### Step 3: Get API Credentials
1. Go to Settings > API
2. Copy your Project URL
3. Copy your `anon` `public` key

#### Step 4: Configure App
1. Open `AppConfig.java`
2. Replace `SUPABASE_URL` with your Project URL
3. Replace `SUPABASE_ANON_KEY` with your anon key

```java
public static final String SUPABASE_URL = "https://your-project.supabase.co";
public static final String SUPABASE_ANON_KEY = "your-anon-key-here";
```

### 2. Android Studio Setup

#### Requirements
- Android Studio Chipmunk 2021.2.1 or newer
- Java JDK 8+
- Android SDK API 24+

#### Build Steps
1. Open project in Android Studio
2. Wait for Gradle sync to complete
3. Build > Clean Project
4. Build > Rebuild Project
5. Run on device/emulator

### 3. Default Login Credentials

After setup, you can login with:

**Manager Account:**
- Username: `manager`
- Password: `password123`

**Staff Account:**
- Username: `staff`  
- Password: `password123`

### 4. Testing Checklist

- [ ] Login with manager account
- [ ] Login with staff account
- [ ] Add products to cart
- [ ] Process checkout
- [ ] View transaction history
- [ ] Manager: Add/edit products
- [ ] Manager: View reports

### 5. Troubleshooting

#### Common Issues

**Build Errors:**
- Clean and rebuild project
- Check Gradle sync
- Update Android SDK if needed

**Network Errors:**
- Verify Supabase URL and key
- Check internet connection
- Verify database is set up correctly

**Login Issues:**
- Check if users table has data
- Verify credentials in database
- Check network connectivity

#### Support
If you encounter issues:
1. Check Android Studio logs
2. Verify Supabase setup
3. Ensure all dependencies are installed
4. Check network permissions in manifest

### 6. Next Steps

After successful setup:
1. Customize product categories
2. Add your own products
3. Configure tax rates if needed
4. Set up receipt printing (optional)
5. Deploy to production devices

## Production Deployment

### Security Considerations
- Change default passwords
- Use environment variables for API keys
- Enable proper RLS policies in Supabase
- Use HTTPS only
- Implement proper session management

### Performance Optimization
- Enable ProGuard/R8 for release builds
- Optimize images and assets
- Test on target devices
- Monitor memory usage

### Maintenance
- Regular database backups
- Monitor API usage
- Update dependencies regularly
- Test new Android versions
