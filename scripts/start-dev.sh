#!/bin/bash

# Nexus Job Board - Development Startup Script
# This script starts the development environment with all required services

set -e

echo "🚀 Starting Nexus Job Board Development Environment..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

# Check if docker-compose is available
if ! command -v docker-compose &> /dev/null; then
    echo "❌ docker-compose is not installed. Please install docker-compose first."
    exit 1
fi

# Create necessary directories
mkdir -p logs
mkdir -p data/postgres
mkdir -p data/redis

echo "📦 Starting infrastructure services (PostgreSQL, Redis, RabbitMQ)..."
docker-compose up -d postgres redis rabbitmq

echo "⏳ Waiting for services to be ready..."
sleep 10

# Check if services are healthy
echo "🔍 Checking service health..."
docker-compose ps

echo "🏗️  Building and starting backend..."
docker-compose up -d backend

echo "⏳ Waiting for backend to be ready..."
sleep 30

echo "🎨 Building and starting frontend..."
docker-compose up -d frontend

echo "✅ All services started successfully!"
echo ""
echo "🌐 Access the application:"
echo "   Frontend: http://localhost:3000"
echo "   Backend API: http://localhost:8080/api/v1"
echo "   Swagger UI: http://localhost:8080/api/v1/swagger-ui.html"
echo "   RabbitMQ Management: http://localhost:15672 (admin/password)"
echo ""
echo "📊 Monitor services:"
echo "   docker-compose logs -f [service-name]"
echo "   docker-compose ps"
echo ""
echo "🛑 Stop services:"
echo "   docker-compose down"
echo ""
echo "🎉 Happy coding with SOLID principles!"
