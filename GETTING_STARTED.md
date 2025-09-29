# Getting Started with Nexus Job Board

## 🚀 Quick Start

### Prerequisites
- **Java 17+** (for backend development)
- **Node.js 18+** (for frontend development)
- **Docker & Docker Compose** (for containerized deployment)
- **Git** (for version control)

### 1. Clone and Setup

```bash
# Navigate to the project
cd /home/ochogwuprince/nexus-job-board-java

# Make scripts executable
chmod +x scripts/start-dev.sh

# Start the development environment
./scripts/start-dev.sh
```

### 2. Access the Application

Once all services are running:

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/api/v1
- **API Documentation**: http://localhost:8080/api/v1/swagger-ui.html
- **RabbitMQ Management**: http://localhost:15672 (admin/password)

### 3. Default Credentials

**Admin User**:
- Email: `admin@nexusjobs.com`
- Password: `admin123`

## 🛠️ Development Setup

### Backend Development

```bash
cd backend

# Install dependencies
./mvnw clean install

# Run tests
./mvnw test

# Start development server
./mvnw spring-boot:run
```

### Frontend Development

```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm start

# Run tests
npm test

# Build for production
npm run build
```

## 🐳 Docker Commands

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f [service-name]

# Stop all services
docker-compose down

# Rebuild and restart
docker-compose up -d --build

# View running services
docker-compose ps
```

## 📊 Monitoring

### Health Checks
- Backend: http://localhost:8080/api/v1/actuator/health
- Database: Check with `docker-compose ps`
- Redis: Check with `docker-compose logs redis`

### Logs
```bash
# Backend logs
docker-compose logs -f backend

# Database logs
docker-compose logs -f postgres

# All services
docker-compose logs -f
```

## 🧪 Testing

### Backend Tests
```bash
cd backend
./mvnw test                    # Unit tests
./mvnw verify                  # Integration tests
./mvnw jacoco:report          # Coverage report
```

### Frontend Tests
```bash
cd frontend
npm test                       # Unit tests
npm run test:coverage         # Coverage report
npm run lint                  # Code linting
```

## 🔧 Configuration

### Environment Variables

Create `.env` file in the root directory:

```env
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=nexus_job_board
DB_USERNAME=postgres
DB_PASSWORD=password

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# RabbitMQ
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=admin
RABBITMQ_PASSWORD=password

# JWT
JWT_SECRET=your-secret-key-here
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# Email (optional)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

## 🎯 API Usage Examples

### Authentication

```bash
# Register new user
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe",
    "role": "JOB_SEEKER"
  }'

# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user@example.com",
    "password": "password123"
  }'
```

### Job Operations

```bash
# Get all jobs
curl -X GET http://localhost:8080/api/v1/jobs

# Search jobs
curl -X GET "http://localhost:8080/api/v1/jobs/search?query=developer"

# Create job (requires authentication)
curl -X POST http://localhost:8080/api/v1/jobs \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "title": "Senior Java Developer",
    "description": "We are looking for a senior Java developer...",
    "companyId": 1,
    "jobType": "FULL_TIME",
    "experienceLevel": "SENIOR_LEVEL",
    "location": "San Francisco, CA"
  }'
```

## 🔍 Troubleshooting

### Common Issues

1. **Port Already in Use**
   ```bash
   # Check what's using the port
   lsof -i :8080
   
   # Kill the process
   kill -9 <PID>
   ```

2. **Database Connection Issues**
   ```bash
   # Check if PostgreSQL is running
   docker-compose ps postgres
   
   # Restart database
   docker-compose restart postgres
   ```

3. **Redis Connection Issues**
   ```bash
   # Check Redis status
   docker-compose logs redis
   
   # Restart Redis
   docker-compose restart redis
   ```

### Reset Development Environment

```bash
# Stop all services
docker-compose down

# Remove volumes (⚠️ This will delete all data)
docker-compose down -v

# Rebuild and start fresh
docker-compose up -d --build
```

## 📚 Additional Resources

- [SOLID Principles Guide](./ARCHITECTURE.md)
- [API Documentation](http://localhost:8080/api/v1/swagger-ui.html)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [React Documentation](https://reactjs.org/docs)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Follow SOLID principles in your implementation
4. Add tests for new functionality
5. Commit your changes (`git commit -m 'Add amazing feature'`)
6. Push to the branch (`git push origin feature/amazing-feature`)
7. Open a Pull Request

## 📞 Support

If you encounter any issues:

1. Check the [troubleshooting section](#-troubleshooting)
2. Review the logs: `docker-compose logs -f`
3. Check the [GitHub Issues](https://github.com/ochogwuprince92/nexus-job-board-java/issues)
4. Contact: ochogwuprince92@gmail.com

---

**Happy coding with SOLID principles! 🎉**
